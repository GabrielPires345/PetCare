package com.petcare.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Async
    public void enviarEmailVerificacao(String destinatario, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String logoUrl = baseUrl + "/images/PetCareLogo.png";

            helper.setTo(destinatario);
            helper.setSubject("PetCare - Verifique seu email");

            String verificationLink = baseUrl + "/api/auth/verificar-email?token=" + token;

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0; padding:0; background-color:#f4f6f9; font-family:Arial, Helvetica, sans-serif;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.08);">

                                    <!-- Header com logo -->
                                    <tr>
                                        <td align="center" style="background-color:#2c7be5; padding:32px 40px;">
                                            <img src="__LOGO_URL__" alt="PetCare" style="max-width:180px; height:auto;" />
                                        </td>
                                    </tr>

                                    <!-- Conteudo -->
                                    <tr>
                                        <td style="padding:40px 48px;">
                                            <h2 style="margin:0 0 8px 0; color:#1a1a2e; font-size:24px; font-weight:700;">
                                                Bem-vindo ao PetCare!
                                            </h2>
                                            <p style="margin:0 0 24px 0; color:#6c757d; font-size:16px; line-height:1.6;">
                                                Olá! Obrigado por se cadastrar na plataforma PetCare.<br/>
                                                Para ativar sua conta e começar a utilizar nossos serviços, confirme seu email abaixo.
                                            </p>

                                            <!-- Botao de verificacao -->
                                            <table width="100%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td align="center" style="padding:8px 0 32px 0;">
                                                        <a href="__VERIFICATION_LINK__"
                                                           style="display:inline-block; background-color:#2c7be5; color:#ffffff; text-decoration:none;
                                                                  padding:14px 40px; border-radius:8px; font-size:16px; font-weight:600;
                                                                  letter-spacing:0.5px;">
                                                            Verificar Email
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="margin:0 0 12px 0; color:#6c757d; font-size:14px; line-height:1.6;">
                                                Se o botão não funcionar, copie e cole o link abaixo no seu navegador:
                                            </p>
                                            <p style="margin:0 0 24px 0; color:#2c7be5; font-size:13px; word-break:break-all;">
                                                __VERIFICATION_LINK__
                                            </p>

                                            <div style="border-top:1px solid #e9ecef; padding-top:20px;">
                                                <p style="margin:0; color:#adb5bd; font-size:13px; line-height:1.5;">
                                                    Se você não criou uma conta no PetCare, pode ignorar este email com segurança.<br/>
                                                    Este link expira após a verificação e não pode ser reutilizado.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color:#f8f9fa; padding:24px 48px; text-align:center;">
                                            <p style="margin:0 0 4px 0; color:#495057; font-size:13px; font-weight:600;">
                                                PetCare
                                            </p>
                                            <p style="margin:0; color:#adb5bd; font-size:12px;">
                                                Cuidando do seu pet com carinho
                                            </p>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.replace("__VERIFICATION_LINK__", verificationLink).replace("__LOGO_URL__", logoUrl);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Erro ao enviar email de verificacao para {}: {}", destinatario, e.getMessage());
        }
    }
}
