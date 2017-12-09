/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.test;

import freemarker.template.Template;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * javamail 测试 由于Spring
 * Boot的starter模块提供了自动化配置，所以在引入了spring-boot-starter-mail依赖之后，
 * 会根据配置文件中的内容去创建JavaMailSender实例， 因此我们可以直接在需要使用的地方直接@Autowired来引入邮件发送对象
 *
 * @author liyong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;  //自动注入

    /**
     * 一封简单邮件的发送
     *
     * @throws Exception
     */
//    @Test
    public void sendSimpleMail() throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("liyong@jingcloud.com");
        message.setTo("liyong@jingcloud.com");
        message.setSubject("主题：简单邮件");
        message.setText("测试邮件内容");
        mailSender.send(message);
    }

    /**
     * 发送附件
     *
     * 在上面单元测试中加入如下测试用例（通过MimeMessageHelper来发送一封带有附件的邮件）：
     *
     * @throws Exception
     */
//    @Test
    public void sendAttachmentsMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("liyong@jingcloud.com");
        helper.setTo("liyong@jingcloud.com");
        helper.setSubject("主题：有附件");
        helper.setText("有附件的邮件");

        /**
         * ClassPathResource：很明显就是类路径资源,我这里的附件是在项目里的,所以需要用ClassPathResource
         * 如果是系统文件资源就不能用ClassPathResource,而要用FileSystemResource,例：
         * FileSystemResource file = new FileSystemResource(new
         * File("D:/Readme.txt"));
         */
//FileSystemResource file = new FileSystemResource(new File("C:\\Users\\ly\\Desktop\\jingvisual\\src\\main\\resources\\static\\a.jpg"));
        ClassPathResource file = new ClassPathResource("static/a.jpg");
        helper.addAttachment("附件-1.jpg", file);
        helper.addAttachment("附件-2.jpg", file);
        mailSender.send(mimeMessage);
    }

    /**
     * 嵌入静态资源,这里需要注意的是addInline函数中资源名称weixin需要与正文中cid:weixin对应起来
     */
//    @Test
    public void sendInlineMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("liyong@jingcloud.com");
        helper.setTo("liyong@jingcloud.com");
        helper.setSubject("主题：嵌入静态资源");
        helper.setText("<html><body><img src=\"cid:weixin\" ></body></html>", true);
//        FileSystemResource file = new FileSystemResource(new File("C:\\Users\\ly\\Desktop\\jingvisual\\src\\main\\resources\\static\\a.jpg"));
        ClassPathResource file = new ClassPathResource("static/a.jpg");
        helper.addInline("weixin", file);
        mailSender.send(mimeMessage);
    }

//    @Test
    public void sendTemplateMail() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("liyong@jingcloud.com");
        helper.setTo("liyong@jingcloud.com");
        helper.setSubject("主题：模板邮件");
        Map<String, Object> model = new HashMap<>();
        model.put("username", "didi");
//        springboot1.5 已经不建议使用Velocity
//        	String text = VelocityEngineUtils.mergeTemplateIntoString(
//			velocityEngine, "template.vm", "UTF-8", model);
        //读取 html 模板 使用  mail.ftl和mail.html都可以   
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("mail.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);
        mailSender.send(mimeMessage);
    }
}
