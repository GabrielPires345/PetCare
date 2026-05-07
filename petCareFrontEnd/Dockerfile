# --- Builder: install PHP extensions ---
FROM php:8.2-apache-bookworm AS builder

RUN apt-get update && apt-get install -y --no-install-recommends \
    libicu-dev libpq-dev libzip-dev \
    && docker-php-ext-install -j$(nproc) intl pdo_pgsql zip opcache \
    && rm -rf /var/lib/apt/lists/*

# --- Runtime ---
FROM php:8.2-apache-bookworm

# Only runtime libs (no -dev packages)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libicu72 libpq5 libzip4 unzip curl \
    && rm -rf /var/lib/apt/lists/*

# Copy compiled extensions from builder
COPY --from=builder /usr/local/lib/php/extensions/ /usr/local/lib/php/extensions/
COPY --from=builder /usr/local/etc/php/conf.d/ /usr/local/etc/php/conf.d/

RUN a2enmod rewrite

# Install Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /var/www/html

# Copy composer files first (layer caching)
COPY composer.json composer.lock ./
RUN composer install --no-scripts --no-autoloader --prefer-dist \
    && composer clear-cache

# Copy rest of the app
COPY . .

# Generate optimized autoloader + set permissions
RUN composer dump-autoload --optimize \
    && mkdir -p var \
    && chown -R www-data:www-data var

# Apache document root -> public/
ENV APACHE_DOCUMENT_ROOT=/var/www/html/public
RUN sed -i 's|/var/www/html|${APACHE_DOCUMENT_ROOT}|g' \
    /etc/apache2/sites-available/000-default.conf \
    /etc/apache2/apache2.conf

EXPOSE 80
