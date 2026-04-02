package br.com.ordempro.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarTesteSimples(String destinatario) {
        String destinoLimpo = destinatario == null ? "" : destinatario.trim();
        String remetenteLimpo = remetente == null ? "" : remetente.trim();

        if (destinoLimpo.isBlank()) {
            throw new IllegalArgumentException("Destinatario vazio");
        }

        if (remetenteLimpo.isBlank()) {
            throw new IllegalArgumentException("Remetente vazio");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetenteLimpo);
        message.setTo(destinoLimpo);
        message.setSubject("Teste OrdemPro");
        message.setText("Teste simples de envio de email.");

        mailSender.send(message);
    }

    public void enviarComPdf(String destinatario, byte[] pdf, Long idOrdem) throws Exception {
        String destinoLimpo = destinatario == null ? "" : destinatario.trim();
        String remetenteLimpo = remetente == null ? "" : remetente.trim();

        if (destinoLimpo.isBlank()) {
            throw new IllegalArgumentException("Destinatario vazio");
        }

        if (remetenteLimpo.isBlank()) {
            throw new IllegalArgumentException("Remetente vazio");
        }

        if (pdf == null || pdf.length == 0) {
            throw new IllegalArgumentException("PDF vazio");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(remetenteLimpo);
        helper.setTo(destinoLimpo);
        helper.setSubject("OrdemPro " + idOrdem);
        helper.setText("Segue em anexo o PDF da ordem de servico.");

        helper.addAttachment(
                "ordempro-" + idOrdem + ".pdf",
                new ByteArrayResource(pdf)
        );

        mailSender.send(message);
    }
}