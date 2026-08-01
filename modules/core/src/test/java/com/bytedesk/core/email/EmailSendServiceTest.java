package com.bytedesk.core.email;

class EmailSendServiceTest {

    // private final EmailSendService emailSendService = new EmailSendService();

    // @Test
    // void shouldMaskAliyunCredentialErrors() {
    //     String message = emailSendService.resolveAliyunEmailErrorMessage("InvalidAccessKeyId.Inactive",
    //             "Specified access key is disabled.");

    //     assertEquals(I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR, message);
    //     assertTrue(emailSendService.isAliyunCredentialOrPermissionError("InvalidAccessKeyId.Inactive"));
    // }

    // @Test
    // void shouldMaskJavaMailAuthenticationErrors() {
    //     String message = emailSendService.resolveEmailExceptionMessage(
    //             new MailAuthenticationException("Authentication failed"));

    //     assertEquals(I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR, message);
    // }

    // @Test
    // void shouldMaskJavaMailTransportErrorsWithGenericMessage() {
    //     String message = emailSendService.resolveEmailExceptionMessage(
    //             new MailSendException("Read timed out"));

    //     assertEquals(I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE, message);
    // }

    // @Test
    // void shouldDetectSmtpConfigErrorsFromNestedMessages() {
    //     boolean configError = emailSendService.isJavaMailConfigError(
    //             new MailSendException("send failed", new RuntimeException("Could not connect to SMTP host: smtp.qq.com")));

    //     assertTrue(configError);
    // }

    // @Test
    // void shouldReturnSuccessForAdminEmailEvenWhenNotWhitelisted() {
    //     BytedeskProperties bytedeskProperties = mock(BytedeskProperties.class);
    //     EmailSendService service = new EmailSendService();
    //     ReflectionTestUtils.setField(service, "bytedeskProperties", bytedeskProperties);

    //     when(bytedeskProperties.isAdminIdentifier("admin@email.com")).thenReturn(true);
    //     when(bytedeskProperties.isInWhitelist("admin@email.com")).thenReturn(false);

    //     EmailSendResult result = service.sendEmailWithResult("admin@email.com", "123456", null);

    //     assertTrue(result.isSuccess());
    // }
}