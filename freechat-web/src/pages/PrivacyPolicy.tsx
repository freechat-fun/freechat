import { Box } from "@mui/material";
import { MarkdownContent } from "../components";

const PRIVACY_POLICY_CONTENT = `
<center><h4>Privacy Policy</h4></center>

<center><span style="text-align: right; font-size: 14px; color: gray;">Last Updated: August 15, 2025</span></center>
<br />

Welcome to FreeChat (accessible at https://freechat.fun), your trusted platform for character AI, chat agents, and role play services. This privacy policy outlines how we collect, use, and protect your information when you use our website and services.

###### 1. Information We Collect
- **Personal Information**: We may collect a few personal information such as your username and platform details. These are collected through forms, cookies, and direct user input.
- **Usage Data**: We collect data such as registrations and interaction details on our site to improve the services. 
- **Meta Information**: Metadata like CSRF tokens are collected to ensure the security and integrity of user interactions.

###### 2. How We Use Your Information
- **Service Provision**: Your personal information is used to provide and enhance our AI interaction services and role-play functionalities.
- **Security and Protection**: Meta information such as CSRF tokens are used to protect against unauthorized access and maintain the security of our services.
- **Communication**: We use collected information to communicate updates, services, and promotional offers when applicable.

###### 3. Sharing Your Information
FreeChat does not sell or rent your personal information to third parties. We may share information to comply with legal obligations or in instances where we collaborate with service providers for operational needs.

###### 4. Cookies and Tracking Technologies
Our website uses cookies to enhance user experience and analyze site traffic. You can manage cookie preferences in your browser settings but note that certain functionalities might be affected.

###### 5. Security of Your Information
We implement industry-standard security measures to ensure the protection of your data. Despite these efforts, no method of transmission over the Internet is 100% secure, and therefore we cannot guarantee absolute security.

###### 6. Your Data Protection Rights
According to applicable data protection laws, you may have rights such as accessing, correcting, or deleting your personal information. For inquiries regarding your data rights, contact us at dev.freechat.fun@gmail.com.

###### 7. Third-Party Links
Our site may contain links to other websites. This privacy policy applies solely to FreeChat. We are not responsible for the privacy practices of other sites.

###### 8. Changes to This Privacy Policy
We may update our privacy policy from time to time. All changes will be posted on this page, and where appropriate, notified to users via email or system notifications.

###### 9. Contact Us
If you have any questions regarding this privacy policy or our privacy practices, please contact us at dev.freechat.fun@gmail.com.
`;

export default function PrivacyPolicy() {
  return (
    <Box sx={{ p: 2 }}>
      <MarkdownContent>
        {PRIVACY_POLICY_CONTENT}
      </MarkdownContent>
    </Box>
  );
}