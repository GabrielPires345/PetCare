@echo off
REM Exporta as imagens Docker do PetCare para transferir para outro computador

echo ============================================
echo   PetCare - Exportar imagens Docker
echo ============================================
echo.

REM Criar pasta de exportacao
set EXPORT_DIR=petcare-docker-export
if exist %EXPORT_DIR% rmdir /s /q %EXPORT_DIR%
mkdir %EXPORT_DIR%

REM Buildar as imagens do backend e frontend
echo [1/8] Construindo imagem do backend...
docker build -t petcare-backend:latest ./Backend
if %errorlevel% neq 0 (
    echo ERRO: Falha ao construir a imagem do backend.
    pause
    exit /b 1
)

echo [2/8] Construindo imagem do frontend...
docker build -t petcare-frontend:latest ./petCareFrontEnd
if %errorlevel% neq 0 (
    echo ERRO: Falha ao construir a imagem do frontend.
    pause
    exit /b 1
)

REM Exportar imagens customizadas
echo [3/8] Exportando imagem do backend...
docker save -o %EXPORT_DIR%\petcare-backend.tar petcare-backend:latest
if %errorlevel% neq 0 (
    echo ERRO: Falha ao exportar imagem do backend.
    pause
    exit /b 1
)

echo [4/8] Exportando imagem do frontend...
docker save -o %EXPORT_DIR%\petcare-frontend.tar petcare-frontend:latest
if %errorlevel% neq 0 (
    echo ERRO: Falha ao exportar imagem do frontend.
    pause
    exit /b 1
)

REM Baixar e exportar imagens de infraestrutura
echo [5/8] Baixando e exportando imagem do PostgreSQL...
docker pull postgres:16
docker save -o %EXPORT_DIR%\postgres.tar postgres:16
if %errorlevel% neq 0 (
    echo ERRO: Falha ao exportar imagem do PostgreSQL.
    pause
    exit /b 1
)

echo [6/8] Baixando e exportando imagem do pgAdmin...
docker pull dpage/pgadmin4:8.6
docker save -o %EXPORT_DIR%\pgadmin.tar dpage/pgadmin4:8.6
if %errorlevel% neq 0 (
    echo ERRO: Falha ao exportar imagem do pgAdmin.
    pause
    exit /b 1
)

echo [7/8] Baixando e exportando imagem do MailHog...
docker pull mailhog/mailhog
docker save -o %EXPORT_DIR%\mailhog.tar mailhog/mailhog
if %errorlevel% neq 0 (
    echo ERRO: Falha ao exportar imagem do MailHog.
    pause
    exit /b 1
)

REM Gerar docker-compose.yml de producao (imagens pre-built) e script de importacao
echo [8/8] Gerando arquivos de configuracao...

(
echo services:
echo   postgres:
echo     image: postgres:16
echo     container_name: petcare-postgres
echo     environment:
echo       POSTGRES_DB: petcare
echo       POSTGRES_USER: postgres
echo       POSTGRES_PASSWORD: postgres
echo     ports:
echo       - "5433:5432"
echo     volumes:
echo       - postgres-data:/var/lib/postgresql/data
echo     networks:
echo       - petcare-net
echo     healthcheck:
echo       test: ["CMD-SHELL", "pg_isready -U postgres"]
echo       interval: 5s
echo       timeout: 3s
echo       retries: 10
echo.
echo   pgadmin:
echo     image: dpage/pgadmin4:8.6
echo     container_name: petcare-pgadmin
echo     environment:
echo       PGADMIN_DEFAULT_EMAIL: admin@petcare.com
echo       PGADMIN_DEFAULT_PASSWORD: admin
echo     ports:
echo       - "5050:80"
echo     depends_on:
echo       postgres:
echo         condition: service_healthy
echo     networks:
echo       - petcare-net
echo.
echo   mailhog:
echo     image: mailhog/mailhog
echo     container_name: petcare-mailhog
echo     ports:
echo       - "1025:1025"
echo       - "8025:8025"
echo     networks:
echo       - petcare-net
echo.
echo   backend:
echo     image: petcare-backend:latest
echo     container_name: petcare-backend
echo     ports:
echo       - "8080:8080"
echo     environment:
echo       SPRING_PROFILES_ACTIVE: docker
echo       APP_BASE_URL: http://localhost:8080
echo     depends_on:
echo       postgres:
echo         condition: service_healthy
echo     networks:
echo       - petcare-net
echo.
echo   frontend:
echo     image: petcare-frontend:latest
echo     container_name: petcare-frontend
echo     ports:
echo       - "8000:80"
echo     environment:
echo       APP_ENV: dev
echo       APP_DEBUG: "1"
echo       APP_SECRET: b749e3d83ad18e65aa70e5d03f95052b
echo       DATABASE_URL: "postgresql://postgres:postgres@postgres:5432/petcare?serverVersion=16^&charset=utf8"
echo       BACKEND_API_URL: http://backend:8080
echo       MAILER_DSN: "smtp://mailhog:1025"
echo     depends_on:
echo       - backend
echo       - postgres
echo     networks:
echo       - petcare-net
echo.
echo volumes:
echo   postgres-data:
echo.
echo networks:
echo   petcare-net:
echo     driver: bridge
) > %EXPORT_DIR%\docker-compose.yml

REM Criar script de importacao
(
echo @echo off
echo REM Importa e roda as imagens Docker do PetCare
echo.
echo echo ============================================
echo echo   PetCare - Importar e iniciar aplicacao
echo echo ============================================
echo echo.
echo.
echo echo [1/6] Carregando imagem do PostgreSQL...
echo docker load -i postgres.tar
echo if %%errorlevel%% neq 0 ^(
echo     echo ERRO: Falha ao carregar imagem do PostgreSQL.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo [2/6] Carregando imagem do pgAdmin...
echo docker load -i pgadmin.tar
echo if %%errorlevel%% neq 0 ^(
echo     echo ERRO: Falha ao carregar imagem do pgAdmin.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo [3/6] Carregando imagem do MailHog...
echo docker load -i mailhog.tar
echo if %%errorlevel%% neq 0 ^(
echo     echo ERRO: Falha ao carregar imagem do MailHog.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo [4/6] Carregando imagem do backend...
echo docker load -i petcare-backend.tar
echo if %%errorlevel%% neq 0 ^(
echo     echo ERRO: Falha ao carregar imagem do backend.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo [5/6] Carregando imagem do frontend...
echo docker load -i petcare-frontend.tar
echo if %%errorlevel%% neq 0 ^(
echo     echo ERRO: Falha ao carregar imagem do frontend.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo echo [6/6] Iniciando aplicacao...
echo docker compose up -d
echo.
echo echo.
echo echo PetCare esta rodando! Acesse:
echo echo   Frontend:  http://localhost:8000
echo echo   Backend:   http://localhost:8080
echo echo   Swagger:   http://localhost:8080/swagger-ui.html
echo echo   pgAdmin:   http://localhost:5050
echo echo   MailHog:   http://localhost:8025
echo echo.
echo echo Parar: docker compose down
echo echo Logs backend:  docker compose logs -f backend
echo echo Logs frontend: docker compose logs -f frontend
echo pause
) > %EXPORT_DIR%\import-docker.bat

echo.
echo ============================================
echo   Exportacao concluida!
echo ============================================
echo.
echo   Pasta: %EXPORT_DIR%\
echo.
echo   Conteudo:
dir /b %EXPORT_DIR%
echo.
echo   Copie a pasta "%EXPORT_DIR%" para o pendrive.
echo   No outro computador, execute import-docker.bat
echo.
pause
