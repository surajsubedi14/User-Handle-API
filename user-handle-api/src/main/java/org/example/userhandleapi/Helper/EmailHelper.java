package org.example.userhandleapi.Helper;

import java.security.SecureRandom;

public class EmailHelper {
    private static final String OTP_CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getEmailBodyForActive(String status, String doctorName, String hospitalName) {
        String actionTaken = status.equalsIgnoreCase("activated") ? "Activated" : "Deactivated";

        return "Dear Recipient,\n\n" +
                "We would like to inform you that the status of the following doctor has been updated:\n\n" +
                "- Doctor Name: " + doctorName + "\n" +
                "- Hospital Name: " + hospitalName + "\n" +
                "\n" +
                "Action Taken: " + actionTaken + "\n\n" +
                doctorName + "'s profile is now " + status + " and ready to serve patients. " +
                "Please ensure that any scheduling or administrative tasks reflect this change accordingly.\n\n" +
                "If you have any questions or require further assistance, please don't hesitate to contact us.\n\n" +
                "Thank you for your attention to this matter.\n\n" +
                "Sincerely,\n" +
                "Your Name\n" +
                "Your Position/Department\n" +
                "Your Contact Information";
    }

    public static String getEmailBodyForDeactive(String status, String doctorName, String hospitalName) {
        return "Dear Recipient,\n\n" +
                "We regret to inform you that the status of the following doctor has been updated:\n\n" +
                "- Doctor Name: " + doctorName + "\n" +
                "- Hospital Name: " + hospitalName + "\n" +
                "\n" +
                "Action Taken: " + status + "\n\n" +
                doctorName + "'s profile is now " + status + " and is no longer available to serve patients. " +
                "Please ensure that any scheduling or administrative tasks reflect this change accordingly.\n\n" +
                "If you have any questions or require further assistance, please don't hesitate to contact us.\n\n" +
                "Thank you for your attention to this matter.\n\n" +
                "Sincerely,\n" +
                "Your Name\n" +
                "Your Position/Department\n" +
                "Your Contact Information";
    }

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(OTP_CHARS.length());
            char otpChar = OTP_CHARS.charAt(randomIndex);
            otp.append(otpChar);
        }
        return otp.toString();
    }

}
