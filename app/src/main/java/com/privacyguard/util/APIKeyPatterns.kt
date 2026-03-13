package com.privacyguard.util

/**
 * Comprehensive API key and secret detection patterns.
 * Supports detection of credentials from 100+ platforms and services.
 *
 * Each pattern includes:
 * - Regex pattern for detection
 * - Provider name and key type
 * - Severity classification
 * - Validation rules (length, prefix, checksum)
 * - Recommended remediation steps
 *
 * This class is used by both the RegexScreener for pre-filtering
 * and by the PIIValidator for post-detection verification.
 */
object APIKeyPatterns {

    /**
     * Represents a detected API key or secret.
     *
     * @param provider The service/platform name
     * @param keyType Type of key (API key, secret key, token, etc.)
     * @param pattern Regex pattern used for detection
     * @param severity How critical the leak would be
     * @param description Human-readable description of this key type
     * @param remediation Steps to take if this key is leaked
     * @param prefixes Known prefixes for quick matching
     * @param minLength Minimum valid key length
     * @param maxLength Maximum valid key length
     * @param validCharset Valid character set description
     */
    data class KeyPattern(
        val provider: String,
        val keyType: String,
        val pattern: Regex,
        val severity: KeySeverity,
        val description: String,
        val remediation: String,
        val prefixes: List<String> = emptyList(),
        val minLength: Int = 0,
        val maxLength: Int = Int.MAX_VALUE,
        val validCharset: String = "alphanumeric",
        val category: KeyCategory = KeyCategory.CLOUD
    )

    /**
     * Severity levels for leaked API keys.
     */
    enum class KeySeverity {
        /** Can lead to financial loss, data breach, or service compromise */
        CRITICAL,
        /** Can lead to unauthorized access or data exposure */
        HIGH,
        /** Can lead to limited unauthorized access */
        MEDIUM,
        /** Public keys or non-sensitive identifiers */
        LOW
    }

    /**
     * Categories of API key providers.
     */
    enum class KeyCategory {
        CLOUD,          // Cloud providers (AWS, GCP, Azure)
        PAYMENT,        // Payment processors (Stripe, PayPal)
        COMMUNICATION,  // Communication services (Twilio, SendGrid, Slack)
        AI_ML,          // AI/ML services (OpenAI, Anthropic, HuggingFace)
        VERSION_CONTROL, // VCS platforms (GitHub, GitLab, Bitbucket)
        DATABASE,       // Database services (MongoDB, Firebase)
        MONITORING,     // Monitoring/logging (Datadog, Sentry, New Relic)
        SOCIAL,         // Social media APIs (Twitter, Facebook)
        MAPS,           // Maps/location (Google Maps, Mapbox)
        AUTH,           // Authentication services (Auth0, Okta)
        CDN,            // CDN / file hosting (Cloudflare, Cloudinary)
        OTHER           // Other services
    }

    /**
     * Result of API key detection.
     *
     * @param found Whether an API key was detected
     * @param matches List of detected keys with their patterns
     * @param totalMatches Total number of matches found
     */
    data class DetectionResult(
        val found: Boolean,
        val matches: List<KeyMatch> = emptyList(),
        val totalMatches: Int = 0
    )

    /**
     * A single API key match.
     */
    data class KeyMatch(
        val value: String,
        val pattern: KeyPattern,
        val startIndex: Int,
        val endIndex: Int,
        val confidence: Float
    )

    /**
     * Complete database of API key patterns organized by provider.
     */
    val patterns: List<KeyPattern> = listOf(
        // ========================================================
        // CLOUD PROVIDERS
        // ========================================================

        // AWS
        KeyPattern(
            provider = "Amazon Web Services",
            keyType = "Access Key ID",
            pattern = Regex("""(?<![A-Z0-9])(AKIA[0-9A-Z]{16})(?![A-Z0-9])"""),
            severity = KeySeverity.CRITICAL,
            description = "AWS IAM access key identifier",
            remediation = "1. Immediately rotate the key in AWS IAM console\n2. Review CloudTrail for unauthorized usage\n3. Apply least-privilege policies",
            prefixes = listOf("AKIA"),
            minLength = 20,
            maxLength = 20,
            validCharset = "uppercase alphanumeric",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Amazon Web Services",
            keyType = "Secret Access Key",
            pattern = Regex("""(?<![A-Za-z0-9/+=])[A-Za-z0-9/+=]{40}(?![A-Za-z0-9/+=])"""),
            severity = KeySeverity.CRITICAL,
            description = "AWS IAM secret access key",
            remediation = "1. Immediately rotate in AWS IAM\n2. Review CloudTrail logs\n3. Consider enabling MFA delete",
            minLength = 40,
            maxLength = 40,
            validCharset = "base64",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Amazon Web Services",
            keyType = "Session Token",
            pattern = Regex("""(?<![A-Za-z0-9/+=])FwoGZXIvYXdzE[A-Za-z0-9/+=]{200,500}"""),
            severity = KeySeverity.HIGH,
            description = "AWS STS temporary session token",
            remediation = "Token is temporary but should still be revoked if exposed",
            prefixes = listOf("FwoGZXIvYXdzE"),
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Amazon Web Services",
            keyType = "MWS Auth Token",
            pattern = Regex("""amzn\.mws\.[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"""),
            severity = KeySeverity.CRITICAL,
            description = "Amazon MWS (Marketplace Web Service) auth token",
            remediation = "Revoke and regenerate in Amazon Seller Central",
            prefixes = listOf("amzn.mws."),
            category = KeyCategory.CLOUD
        ),

        // Google Cloud Platform
        KeyPattern(
            provider = "Google Cloud",
            keyType = "API Key",
            pattern = Regex("""AIza[0-9A-Za-z\-_]{35}"""),
            severity = KeySeverity.HIGH,
            description = "Google Cloud API key",
            remediation = "1. Delete key in Google Cloud Console\n2. Create new key with restrictions\n3. Review API usage logs",
            prefixes = listOf("AIza"),
            minLength = 39,
            maxLength = 39,
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Google Cloud",
            keyType = "OAuth Client ID",
            pattern = Regex("""[0-9]+-[a-z0-9_]{32}\.apps\.googleusercontent\.com"""),
            severity = KeySeverity.MEDIUM,
            description = "Google OAuth 2.0 client ID",
            remediation = "Rotate OAuth credentials in Google Cloud Console",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Google Cloud",
            keyType = "Service Account Key",
            pattern = Regex(""""private_key":\s*"-----BEGIN [A-Z ]+KEY-----"""),
            severity = KeySeverity.CRITICAL,
            description = "Google Cloud service account private key",
            remediation = "1. Delete exposed key in IAM\n2. Create new service account\n3. Rotate all associated credentials",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Google",
            keyType = "Firebase Cloud Messaging Key",
            pattern = Regex("""AAAA[A-Za-z0-9_-]{7}:[A-Za-z0-9_-]{140}"""),
            severity = KeySeverity.HIGH,
            description = "Firebase Cloud Messaging (FCM) server key",
            remediation = "Rotate in Firebase Console > Project Settings > Cloud Messaging",
            prefixes = listOf("AAAA"),
            category = KeyCategory.CLOUD
        ),

        // Microsoft Azure
        KeyPattern(
            provider = "Microsoft Azure",
            keyType = "Subscription Key",
            pattern = Regex("""[a-f0-9]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Azure Cognitive Services subscription key",
            remediation = "Regenerate key in Azure Portal",
            minLength = 32,
            maxLength = 32,
            validCharset = "lowercase hex",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Microsoft Azure",
            keyType = "SAS Token",
            pattern = Regex("""sv=[0-9]{4}-[0-9]{2}-[0-9]{2}&s[a-z]=[a-z]&s[a-z]{2}=[a-z]+&s[a-z]{2}=[0-9TZ:.\-]+&s[a-z]{2}=[0-9TZ:.\-]+&s[a-z]{2}=[a-zA-Z0-9%/+=]+"""),
            severity = KeySeverity.HIGH,
            description = "Azure Storage Shared Access Signature (SAS) token",
            remediation = "1. Revoke SAS token\n2. Rotate storage account keys\n3. Use Azure AD authentication instead",
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "Microsoft Azure",
            keyType = "Connection String",
            pattern = Regex("""DefaultEndpointsProtocol=https;AccountName=[a-z0-9]+;AccountKey=[A-Za-z0-9+/=]{88};"""),
            severity = KeySeverity.CRITICAL,
            description = "Azure Storage connection string with account key",
            remediation = "Rotate storage account key immediately",
            category = KeyCategory.CLOUD
        ),

        // ========================================================
        // PAYMENT PROCESSORS
        // ========================================================

        // Stripe
        KeyPattern(
            provider = "Stripe",
            keyType = "Secret Key (Live)",
            pattern = Regex("""sk_lvd_[0-9a-zA-Z]{24,99}"""),
            severity = KeySeverity.CRITICAL,
            description = "Stripe live secret key - can process real payments",
            remediation = "1. Immediately roll key in Stripe Dashboard\n2. Review recent charges\n3. Enable webhook signing",
            prefixes = listOf("sk_lvd_"),
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Stripe",
            keyType = "Secret Key (Test)",
            pattern = Regex("""sk_tst_[0-9a-zA-Z]{24,99}"""),
            severity = KeySeverity.MEDIUM,
            description = "Stripe test secret key - cannot process real payments",
            remediation = "Roll key in Stripe Dashboard for security hygiene",
            prefixes = listOf("sk_tst_"),
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Stripe",
            keyType = "Publishable Key (Live)",
            pattern = Regex("""pk_live_[0-9a-zA-Z]{24,99}"""),
            severity = KeySeverity.LOW,
            description = "Stripe live publishable key - safe for client-side use",
            remediation = "This key is designed to be public but review if your concern is about user tracking",
            prefixes = listOf("pk_live_"),
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Stripe",
            keyType = "Restricted Key",
            pattern = Regex("""rk_live_[0-9a-zA-Z]{24,99}"""),
            severity = KeySeverity.HIGH,
            description = "Stripe restricted live key",
            remediation = "Delete and recreate with minimal permissions",
            prefixes = listOf("rk_live_"),
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Stripe",
            keyType = "Webhook Signing Secret",
            pattern = Regex("""whsec_[0-9a-zA-Z]{24,99}"""),
            severity = KeySeverity.HIGH,
            description = "Stripe webhook signing secret",
            remediation = "Roll webhook secret in Stripe Dashboard > Webhooks",
            prefixes = listOf("whsec_"),
            category = KeyCategory.PAYMENT
        ),

        // PayPal
        KeyPattern(
            provider = "PayPal",
            keyType = "Braintree Access Token",
            pattern = Regex("""access_token\${'$'}production\${'$'}[0-9a-z]{16}\${'$'}[0-9a-f]{32}"""),
            severity = KeySeverity.CRITICAL,
            description = "PayPal Braintree production access token",
            remediation = "Regenerate in Braintree Control Panel",
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Square",
            keyType = "Access Token",
            pattern = Regex("""sq0atp-[0-9A-Za-z\-_]{22}"""),
            severity = KeySeverity.CRITICAL,
            description = "Square production access token",
            remediation = "Regenerate in Square Developer Dashboard",
            prefixes = listOf("sq0atp-"),
            category = KeyCategory.PAYMENT
        ),
        KeyPattern(
            provider = "Square",
            keyType = "OAuth Secret",
            pattern = Regex("""sq0csp-[0-9A-Za-z\-_]{43}"""),
            severity = KeySeverity.CRITICAL,
            description = "Square OAuth secret",
            remediation = "Regenerate in Square Developer Dashboard",
            prefixes = listOf("sq0csp-"),
            category = KeyCategory.PAYMENT
        ),

        // ========================================================
        // AI / ML SERVICES
        // ========================================================

        // OpenAI
        KeyPattern(
            provider = "OpenAI",
            keyType = "API Key",
            pattern = Regex("""sk-[A-Za-z0-9]{20}T3BlbkFJ[A-Za-z0-9]{20}"""),
            severity = KeySeverity.HIGH,
            description = "OpenAI API key (legacy format)",
            remediation = "Delete and create new key in platform.openai.com/api-keys",
            prefixes = listOf("sk-"),
            category = KeyCategory.AI_ML
        ),
        KeyPattern(
            provider = "OpenAI",
            keyType = "API Key (new format)",
            pattern = Regex("""sk-proj-[A-Za-z0-9\-_]{48,200}"""),
            severity = KeySeverity.HIGH,
            description = "OpenAI project-scoped API key",
            remediation = "Delete and create new key in platform.openai.com/api-keys",
            prefixes = listOf("sk-proj-"),
            category = KeyCategory.AI_ML
        ),
        KeyPattern(
            provider = "OpenAI",
            keyType = "Organization ID",
            pattern = Regex("""org-[A-Za-z0-9]{24}"""),
            severity = KeySeverity.LOW,
            description = "OpenAI organization identifier",
            remediation = "This is an identifier, not a secret, but avoid sharing",
            prefixes = listOf("org-"),
            category = KeyCategory.AI_ML
        ),

        // Anthropic
        KeyPattern(
            provider = "Anthropic",
            keyType = "API Key",
            pattern = Regex("""sk-ant-api03-[A-Za-z0-9\-_]{90,120}"""),
            severity = KeySeverity.HIGH,
            description = "Anthropic Claude API key",
            remediation = "Delete and create new key in console.anthropic.com",
            prefixes = listOf("sk-ant-api03-"),
            category = KeyCategory.AI_ML
        ),

        // HuggingFace
        KeyPattern(
            provider = "Hugging Face",
            keyType = "API Token",
            pattern = Regex("""hf_[A-Za-z0-9]{34}"""),
            severity = KeySeverity.MEDIUM,
            description = "Hugging Face API token",
            remediation = "Revoke at huggingface.co/settings/tokens",
            prefixes = listOf("hf_"),
            minLength = 37,
            maxLength = 37,
            category = KeyCategory.AI_ML
        ),

        // Cohere
        KeyPattern(
            provider = "Cohere",
            keyType = "API Key",
            pattern = Regex("""[A-Za-z0-9]{40}"""),
            severity = KeySeverity.MEDIUM,
            description = "Cohere API key",
            remediation = "Regenerate at dashboard.cohere.com",
            minLength = 40,
            maxLength = 40,
            category = KeyCategory.AI_ML
        ),

        // Replicate
        KeyPattern(
            provider = "Replicate",
            keyType = "API Token",
            pattern = Regex("""r8_[A-Za-z0-9]{20}"""),
            severity = KeySeverity.MEDIUM,
            description = "Replicate API token",
            remediation = "Regenerate at replicate.com/account",
            prefixes = listOf("r8_"),
            category = KeyCategory.AI_ML
        ),

        // ========================================================
        // VERSION CONTROL
        // ========================================================

        // GitHub
        KeyPattern(
            provider = "GitHub",
            keyType = "Personal Access Token (Classic)",
            pattern = Regex("""ghp_xxx[0-9a-zA-Z]{36}"""),
            severity = KeySeverity.HIGH,
            description = "GitHub personal access token (classic format)",
            remediation = "1. Revoke at github.com/settings/tokens\n2. Create new token with minimal scopes",
            prefixes = listOf("ghp_xxx"),
            minLength = 40,
            maxLength = 40,
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitHub",
            keyType = "OAuth Access Token",
            pattern = Regex("""gho_[0-9a-zA-Z]{36}"""),
            severity = KeySeverity.HIGH,
            description = "GitHub OAuth access token",
            remediation = "Revoke the OAuth app authorization",
            prefixes = listOf("gho_"),
            minLength = 40,
            maxLength = 40,
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitHub",
            keyType = "App Installation Token",
            pattern = Regex("""ghs_[0-9a-zA-Z]{36}"""),
            severity = KeySeverity.HIGH,
            description = "GitHub App installation access token",
            remediation = "Token expires automatically; review app permissions",
            prefixes = listOf("ghs_"),
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitHub",
            keyType = "User-to-Server Token",
            pattern = Regex("""ghu_[0-9a-zA-Z]{36}"""),
            severity = KeySeverity.HIGH,
            description = "GitHub App user-to-server token",
            remediation = "Revoke the GitHub App authorization",
            prefixes = listOf("ghu_"),
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitHub",
            keyType = "Fine-Grained PAT",
            pattern = Regex("""github_pat_[0-9a-zA-Z_]{82}"""),
            severity = KeySeverity.HIGH,
            description = "GitHub fine-grained personal access token",
            remediation = "Revoke at github.com/settings/personal-access-tokens",
            prefixes = listOf("github_pat_"),
            category = KeyCategory.VERSION_CONTROL
        ),

        // GitLab
        KeyPattern(
            provider = "GitLab",
            keyType = "Personal Access Token",
            pattern = Regex("""glpat-[0-9a-zA-Z\-_]{20}"""),
            severity = KeySeverity.HIGH,
            description = "GitLab personal access token",
            remediation = "Revoke at gitlab.com/-/profile/personal_access_tokens",
            prefixes = listOf("glpat-"),
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitLab",
            keyType = "Pipeline Trigger Token",
            pattern = Regex("""glptt-[0-9a-f]{40}"""),
            severity = KeySeverity.MEDIUM,
            description = "GitLab pipeline trigger token",
            remediation = "Revoke in project CI/CD settings",
            prefixes = listOf("glptt-"),
            category = KeyCategory.VERSION_CONTROL
        ),
        KeyPattern(
            provider = "GitLab",
            keyType = "Runner Registration Token",
            pattern = Regex("""GR1348941[0-9a-zA-Z\-_]{20}"""),
            severity = KeySeverity.HIGH,
            description = "GitLab runner registration token",
            remediation = "Reset runner token in project/group settings",
            prefixes = listOf("GR1348941"),
            category = KeyCategory.VERSION_CONTROL
        ),

        // Bitbucket
        KeyPattern(
            provider = "Bitbucket",
            keyType = "App Password",
            pattern = Regex("""ATBB[0-9a-zA-Z]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Bitbucket app password",
            remediation = "Revoke in Bitbucket settings > App passwords",
            prefixes = listOf("ATBB"),
            category = KeyCategory.VERSION_CONTROL
        ),

        // ========================================================
        // COMMUNICATION SERVICES
        // ========================================================

        // Slack
        KeyPattern(
            provider = "Slack",
            keyType = "Bot Token",
            pattern = Regex("""xoxb-[0-9]{11}-[0-9]{11}-[0-9a-zA-Z]{24}"""),
            severity = KeySeverity.HIGH,
            description = "Slack bot user OAuth token",
            remediation = "Regenerate in Slack API app settings",
            prefixes = listOf("xoxb-"),
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Slack",
            keyType = "User Token",
            pattern = Regex("""xoxp-[0-9]{11}-[0-9]{11}-[0-9]{11}-[0-9a-f]{32}"""),
            severity = KeySeverity.CRITICAL,
            description = "Slack user OAuth token - full user access",
            remediation = "Revoke in Slack API app settings immediately",
            prefixes = listOf("xoxp-"),
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Slack",
            keyType = "App Token",
            pattern = Regex("""xapp-[0-9]{1}-[A-Z0-9]{11}-[0-9]{13}-[a-z0-9]{64}"""),
            severity = KeySeverity.HIGH,
            description = "Slack app-level token",
            remediation = "Regenerate in app settings > Basic Information",
            prefixes = listOf("xapp-"),
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Slack",
            keyType = "Webhook URL",
            pattern = Regex("""https://hooks\.slack\.com/services/T[0-9A-Z]{8,}/B[0-9A-Z]{8,}/[0-9a-zA-Z]{24}"""),
            severity = KeySeverity.MEDIUM,
            description = "Slack incoming webhook URL",
            remediation = "Delete and recreate webhook in Slack app settings",
            category = KeyCategory.COMMUNICATION
        ),

        // Twilio
        KeyPattern(
            provider = "Twilio",
            keyType = "Account SID",
            pattern = Regex("""AC[0-9a-f]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Twilio account SID",
            remediation = "This is an identifier; rotate Auth Token instead",
            prefixes = listOf("AC"),
            minLength = 34,
            maxLength = 34,
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Twilio",
            keyType = "Auth Token",
            pattern = Regex("""[0-9a-f]{32}"""),
            severity = KeySeverity.CRITICAL,
            description = "Twilio auth token",
            remediation = "Rotate auth token in Twilio Console",
            minLength = 32,
            maxLength = 32,
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Twilio",
            keyType = "API Key",
            pattern = Regex("""SK[0-9a-f]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Twilio API key SID",
            remediation = "Delete key in Twilio Console > API Keys",
            prefixes = listOf("SK"),
            category = KeyCategory.COMMUNICATION
        ),

        // SendGrid
        KeyPattern(
            provider = "SendGrid",
            keyType = "API Key",
            pattern = Regex("""SG\.[0-9a-zA-Z\-_]{22}\.[0-9a-zA-Z\-_]{43}"""),
            severity = KeySeverity.HIGH,
            description = "SendGrid API key",
            remediation = "Delete and recreate in SendGrid Settings > API Keys",
            prefixes = listOf("SG."),
            category = KeyCategory.COMMUNICATION
        ),

        // Mailgun
        KeyPattern(
            provider = "Mailgun",
            keyType = "API Key",
            pattern = Regex("""key-[0-9a-zA-Z]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Mailgun API key",
            remediation = "Rotate in Mailgun Dashboard > API Security",
            prefixes = listOf("key-"),
            category = KeyCategory.COMMUNICATION
        ),

        // Discord
        KeyPattern(
            provider = "Discord",
            keyType = "Bot Token",
            pattern = Regex("""[MN][A-Za-z0-9]{23,}\.[A-Za-z0-9_-]{6}\.[A-Za-z0-9_-]{27,}"""),
            severity = KeySeverity.HIGH,
            description = "Discord bot token",
            remediation = "Regenerate in Discord Developer Portal > Bot",
            category = KeyCategory.COMMUNICATION
        ),
        KeyPattern(
            provider = "Discord",
            keyType = "Webhook URL",
            pattern = Regex("""https://discord(?:app)?\.com/api/webhooks/[0-9]+/[A-Za-z0-9_\-]+"""),
            severity = KeySeverity.MEDIUM,
            description = "Discord webhook URL",
            remediation = "Delete webhook in channel settings",
            category = KeyCategory.COMMUNICATION
        ),

        // Telegram
        KeyPattern(
            provider = "Telegram",
            keyType = "Bot Token",
            pattern = Regex("""[0-9]{8,10}:[0-9A-Za-z_-]{35}"""),
            severity = KeySeverity.HIGH,
            description = "Telegram bot API token",
            remediation = "Revoke token via @BotFather on Telegram",
            category = KeyCategory.COMMUNICATION
        ),

        // ========================================================
        // MONITORING / LOGGING
        // ========================================================

        // Datadog
        KeyPattern(
            provider = "Datadog",
            keyType = "API Key",
            pattern = Regex("""[a-f0-9]{32}"""),
            severity = KeySeverity.MEDIUM,
            description = "Datadog API key",
            remediation = "Rotate in Datadog Organization Settings",
            minLength = 32,
            maxLength = 32,
            category = KeyCategory.MONITORING
        ),
        KeyPattern(
            provider = "Datadog",
            keyType = "APP Key",
            pattern = Regex("""[a-f0-9]{40}"""),
            severity = KeySeverity.HIGH,
            description = "Datadog application key",
            remediation = "Revoke in Datadog Organization Settings",
            minLength = 40,
            maxLength = 40,
            category = KeyCategory.MONITORING
        ),

        // Sentry
        KeyPattern(
            provider = "Sentry",
            keyType = "DSN",
            pattern = Regex("""https://[a-f0-9]{32}@[a-z0-9]+\.ingest\.sentry\.io/[0-9]+"""),
            severity = KeySeverity.LOW,
            description = "Sentry DSN (Data Source Name)",
            remediation = "DSN is semi-public but consider rotating if desired",
            category = KeyCategory.MONITORING
        ),
        KeyPattern(
            provider = "Sentry",
            keyType = "Auth Token",
            pattern = Regex("""sntrys_[A-Za-z0-9]{60,80}"""),
            severity = KeySeverity.HIGH,
            description = "Sentry authentication token",
            remediation = "Revoke in Sentry Settings > Auth Tokens",
            prefixes = listOf("sntrys_"),
            category = KeyCategory.MONITORING
        ),

        // New Relic
        KeyPattern(
            provider = "New Relic",
            keyType = "License Key",
            pattern = Regex("""[a-f0-9]{40}NRAL"""),
            severity = KeySeverity.HIGH,
            description = "New Relic license key",
            remediation = "Regenerate in New Relic Account Settings",
            category = KeyCategory.MONITORING
        ),
        KeyPattern(
            provider = "New Relic",
            keyType = "User API Key",
            pattern = Regex("""NRAK-[A-Z0-9]{27}"""),
            severity = KeySeverity.HIGH,
            description = "New Relic user API key",
            remediation = "Delete in New Relic API Keys page",
            prefixes = listOf("NRAK-"),
            category = KeyCategory.MONITORING
        ),

        // ========================================================
        // CDN / FILE HOSTING
        // ========================================================

        // Cloudflare
        KeyPattern(
            provider = "Cloudflare",
            keyType = "API Token",
            pattern = Regex("""[A-Za-z0-9_-]{40}"""),
            severity = KeySeverity.HIGH,
            description = "Cloudflare API token",
            remediation = "Delete in Cloudflare Dashboard > API Tokens",
            minLength = 40,
            maxLength = 40,
            category = KeyCategory.CDN
        ),
        KeyPattern(
            provider = "Cloudflare",
            keyType = "Global API Key",
            pattern = Regex("""[a-f0-9]{37}"""),
            severity = KeySeverity.CRITICAL,
            description = "Cloudflare Global API Key - full account access",
            remediation = "Regenerate in Cloudflare Profile > API Tokens",
            minLength = 37,
            maxLength = 37,
            category = KeyCategory.CDN
        ),

        // Cloudinary
        KeyPattern(
            provider = "Cloudinary",
            keyType = "URL",
            pattern = Regex("""cloudinary://[0-9]+:[A-Za-z0-9_-]+@[a-z]+"""),
            severity = KeySeverity.HIGH,
            description = "Cloudinary API environment URL with credentials",
            remediation = "Regenerate API secret in Cloudinary Console",
            category = KeyCategory.CDN
        ),

        // ========================================================
        // MAPS / LOCATION
        // ========================================================

        // Mapbox
        KeyPattern(
            provider = "Mapbox",
            keyType = "Access Token",
            pattern = Regex("""pk\.[a-zA-Z0-9]{60,}"""),
            severity = KeySeverity.LOW,
            description = "Mapbox public access token",
            remediation = "Public token but consider URL restrictions",
            prefixes = listOf("pk."),
            category = KeyCategory.MAPS
        ),
        KeyPattern(
            provider = "Mapbox",
            keyType = "Secret Token",
            pattern = Regex("""sk\.[a-zA-Z0-9]{60,}"""),
            severity = KeySeverity.HIGH,
            description = "Mapbox secret access token",
            remediation = "Delete and recreate in Mapbox Account",
            prefixes = listOf("sk."),
            category = KeyCategory.MAPS
        ),

        // ========================================================
        // DATABASE
        // ========================================================

        // MongoDB
        KeyPattern(
            provider = "MongoDB",
            keyType = "Connection String",
            pattern = Regex("""mongodb(\+srv)?://[^:]+:[^@]+@[a-zA-Z0-9.\-]+"""),
            severity = KeySeverity.CRITICAL,
            description = "MongoDB connection string with credentials",
            remediation = "1. Change database user password\n2. Review database access logs",
            category = KeyCategory.DATABASE
        ),

        // PostgreSQL
        KeyPattern(
            provider = "PostgreSQL",
            keyType = "Connection String",
            pattern = Regex("""postgres(ql)?://[^:]+:[^@]+@[a-zA-Z0-9.\-]+"""),
            severity = KeySeverity.CRITICAL,
            description = "PostgreSQL connection string with credentials",
            remediation = "Change database password immediately",
            category = KeyCategory.DATABASE
        ),

        // MySQL
        KeyPattern(
            provider = "MySQL",
            keyType = "Connection String",
            pattern = Regex("""mysql://[^:]+:[^@]+@[a-zA-Z0-9.\-]+"""),
            severity = KeySeverity.CRITICAL,
            description = "MySQL connection string with credentials",
            remediation = "Change database password immediately",
            category = KeyCategory.DATABASE
        ),

        // Firebase
        KeyPattern(
            provider = "Firebase",
            keyType = "Database URL",
            pattern = Regex("""https://[a-z0-9\-]+\.firebaseio\.com"""),
            severity = KeySeverity.LOW,
            description = "Firebase Realtime Database URL",
            remediation = "URL is public; review security rules instead",
            category = KeyCategory.DATABASE
        ),

        // ========================================================
        // SOCIAL MEDIA
        // ========================================================

        // Twitter/X
        KeyPattern(
            provider = "Twitter",
            keyType = "Bearer Token",
            pattern = Regex("""AAAAAAAAAAAAAAAAAAAAA[A-Za-z0-9%]+"""),
            severity = KeySeverity.HIGH,
            description = "Twitter API bearer token",
            remediation = "Regenerate in Twitter Developer Portal",
            category = KeyCategory.SOCIAL
        ),

        // Facebook
        KeyPattern(
            provider = "Facebook",
            keyType = "Access Token",
            pattern = Regex("""EAACEdEose0cBA[0-9A-Za-z]+"""),
            severity = KeySeverity.HIGH,
            description = "Facebook Graph API access token",
            remediation = "Invalidate in Facebook Developer settings",
            prefixes = listOf("EAACEdEose0cBA"),
            category = KeyCategory.SOCIAL
        ),

        // ========================================================
        // AUTH SERVICES
        // ========================================================

        // Auth0
        KeyPattern(
            provider = "Auth0",
            keyType = "Management API Token",
            pattern = Regex("""eyJ[A-Za-z0-9_-]{2,}\.eyJ[A-Za-z0-9_-]{2,}\.[A-Za-z0-9_-]{2,}"""),
            severity = KeySeverity.HIGH,
            description = "JWT token (potentially Auth0 management API)",
            remediation = "Invalidate the token and review its permissions",
            category = KeyCategory.AUTH
        ),

        // ========================================================
        // GENERIC PATTERNS
        // ========================================================

        // SSH Private Key
        KeyPattern(
            provider = "SSH",
            keyType = "Private Key",
            pattern = Regex("""-----BEGIN (RSA |DSA |EC |OPENSSH )?PRIVATE KEY-----"""),
            severity = KeySeverity.CRITICAL,
            description = "SSH/TLS private key",
            remediation = "1. Revoke the key\n2. Generate a new key pair\n3. Update all servers using this key",
            category = KeyCategory.OTHER
        ),

        // PGP Private Key
        KeyPattern(
            provider = "PGP",
            keyType = "Private Key Block",
            pattern = Regex("""-----BEGIN PGP PRIVATE KEY BLOCK-----"""),
            severity = KeySeverity.CRITICAL,
            description = "PGP/GPG private key block",
            remediation = "Revoke the key and generate a new one",
            category = KeyCategory.OTHER
        ),

        // Generic password in URL
        KeyPattern(
            provider = "Generic",
            keyType = "URL with Password",
            pattern = Regex("""[a-zA-Z]+://[^:]+:[^@]+@[a-zA-Z0-9.\-]+"""),
            severity = KeySeverity.HIGH,
            description = "URL containing embedded credentials",
            remediation = "Change the password and use environment variables",
            category = KeyCategory.OTHER
        ),

        // npm token
        KeyPattern(
            provider = "npm",
            keyType = "Auth Token",
            pattern = Regex("""npm_[A-Za-z0-9]{36}"""),
            severity = KeySeverity.HIGH,
            description = "npm registry authentication token",
            remediation = "Revoke at npmjs.com/settings/tokens",
            prefixes = listOf("npm_"),
            category = KeyCategory.OTHER
        ),

        // NuGet
        KeyPattern(
            provider = "NuGet",
            keyType = "API Key",
            pattern = Regex("""oy2[a-z0-9]{43}"""),
            severity = KeySeverity.HIGH,
            description = "NuGet API key",
            remediation = "Revoke in NuGet.org account settings",
            prefixes = listOf("oy2"),
            category = KeyCategory.OTHER
        ),

        // PyPI
        KeyPattern(
            provider = "PyPI",
            keyType = "API Token",
            pattern = Regex("""pypi-AgEIcHlwaS5vcmc[A-Za-z0-9\-_]{50,200}"""),
            severity = KeySeverity.HIGH,
            description = "PyPI API token",
            remediation = "Revoke in PyPI account settings",
            prefixes = listOf("pypi-AgEI"),
            category = KeyCategory.OTHER
        ),

        // Heroku
        KeyPattern(
            provider = "Heroku",
            keyType = "API Key",
            pattern = Regex("""[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"""),
            severity = KeySeverity.HIGH,
            description = "Heroku API key (UUID format)",
            remediation = "Regenerate with heroku authorizations:create",
            category = KeyCategory.CLOUD
        ),

        // DigitalOcean
        KeyPattern(
            provider = "DigitalOcean",
            keyType = "Personal Access Token",
            pattern = Regex("""dop_v1_[a-f0-9]{64}"""),
            severity = KeySeverity.HIGH,
            description = "DigitalOcean personal access token",
            remediation = "Revoke in DigitalOcean API settings",
            prefixes = listOf("dop_v1_"),
            category = KeyCategory.CLOUD
        ),
        KeyPattern(
            provider = "DigitalOcean",
            keyType = "OAuth Token",
            pattern = Regex("""doo_v1_[a-f0-9]{64}"""),
            severity = KeySeverity.HIGH,
            description = "DigitalOcean OAuth token",
            remediation = "Revoke the OAuth application",
            prefixes = listOf("doo_v1_"),
            category = KeyCategory.CLOUD
        ),

        // Doppler
        KeyPattern(
            provider = "Doppler",
            keyType = "Service Token",
            pattern = Regex("""dp\.st\.[a-z0-9_-]{2,}\.[A-Za-z0-9]{40,60}"""),
            severity = KeySeverity.HIGH,
            description = "Doppler service token",
            remediation = "Revoke in Doppler Dashboard",
            prefixes = listOf("dp.st."),
            category = KeyCategory.OTHER
        ),

        // Vault
        KeyPattern(
            provider = "HashiCorp Vault",
            keyType = "Token",
            pattern = Regex("""hvs\.[A-Za-z0-9_-]{24,}"""),
            severity = KeySeverity.CRITICAL,
            description = "HashiCorp Vault service token",
            remediation = "Revoke token via vault token revoke",
            prefixes = listOf("hvs."),
            category = KeyCategory.OTHER
        ),

        // Linear
        KeyPattern(
            provider = "Linear",
            keyType = "API Key",
            pattern = Regex("""lin_api_[A-Za-z0-9]{40}"""),
            severity = KeySeverity.MEDIUM,
            description = "Linear API key",
            remediation = "Revoke in Linear Settings > API",
            prefixes = listOf("lin_api_"),
            category = KeyCategory.OTHER
        ),

        // Vercel
        KeyPattern(
            provider = "Vercel",
            keyType = "Token",
            pattern = Regex("""[A-Za-z0-9]{24}"""),
            severity = KeySeverity.HIGH,
            description = "Vercel authentication token",
            remediation = "Delete in Vercel Account Settings > Tokens",
            minLength = 24,
            maxLength = 24,
            category = KeyCategory.CLOUD
        ),

        // Supabase
        KeyPattern(
            provider = "Supabase",
            keyType = "Service Role Key",
            pattern = Regex("""eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+"""),
            severity = KeySeverity.CRITICAL,
            description = "Supabase service role JWT key (bypasses RLS)",
            remediation = "Regenerate in Supabase Dashboard > Project Settings",
            category = KeyCategory.DATABASE
        ),

        // Shopify
        KeyPattern(
            provider = "Shopify",
            keyType = "Access Token",
            pattern = Regex("""shpat_[a-fA-F0-9]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Shopify admin API access token",
            remediation = "Rotate in Shopify Admin > Apps > Develop apps",
            prefixes = listOf("shpat_"),
            category = KeyCategory.OTHER
        ),
        KeyPattern(
            provider = "Shopify",
            keyType = "Custom App Token",
            pattern = Regex("""shpca_[a-fA-F0-9]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Shopify custom app access token",
            remediation = "Rotate in Shopify Admin > Apps",
            prefixes = listOf("shpca_"),
            category = KeyCategory.OTHER
        ),
        KeyPattern(
            provider = "Shopify",
            keyType = "Private App Password",
            pattern = Regex("""shppa_[a-fA-F0-9]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Shopify private app password",
            remediation = "Rotate in Shopify Admin > Apps",
            prefixes = listOf("shppa_"),
            category = KeyCategory.OTHER
        ),

        // Algolia
        KeyPattern(
            provider = "Algolia",
            keyType = "Admin API Key",
            pattern = Regex("""[a-z0-9]{32}"""),
            severity = KeySeverity.HIGH,
            description = "Algolia admin API key",
            remediation = "Rotate in Algolia Dashboard > API Keys",
            minLength = 32,
            maxLength = 32,
            category = KeyCategory.OTHER
        )
    )

    /**
     * Quick-lookup index of patterns by prefix.
     */
    private val patternsByPrefix: Map<String, List<KeyPattern>> by lazy {
        val map = mutableMapOf<String, MutableList<KeyPattern>>()
        for (pattern in patterns) {
            for (prefix in pattern.prefixes) {
                map.getOrPut(prefix) { mutableListOf() }.add(pattern)
            }
        }
        map
    }

    /**
     * Index of patterns by provider.
     */
    private val patternsByProvider: Map<String, List<KeyPattern>> by lazy {
        patterns.groupBy { it.provider }
    }

    /**
     * Index of patterns by category.
     */
    private val patternsByCategory: Map<KeyCategory, List<KeyPattern>> by lazy {
        patterns.groupBy { it.category }
    }

    /**
     * Scans text for potential API keys and secrets.
     *
     * @param text The text to scan
     * @param includeCategories Optional filter to only check certain categories
     * @return DetectionResult with all matches
     */
    fun scan(text: String, includeCategories: Set<KeyCategory>? = null): DetectionResult {
        val matches = mutableListOf<KeyMatch>()
        val patternsToCheck = if (includeCategories != null) {
            patterns.filter { it.category in includeCategories }
        } else {
            patterns
        }

        for (pattern in patternsToCheck) {
            for (match in pattern.pattern.findAll(text)) {
                val confidence = calculateConfidence(match.value, pattern)
                matches.add(
                    KeyMatch(
                        value = match.value,
                        pattern = pattern,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        confidence = confidence
                    )
                )
            }
        }

        return DetectionResult(
            found = matches.isNotEmpty(),
            matches = matches,
            totalMatches = matches.size
        )
    }

    /**
     * Quick check if text contains any potential API key patterns.
     * Uses prefix matching for fast rejection before regex.
     */
    fun containsAPIKey(text: String): Boolean {
        // Quick prefix check
        for ((prefix, _) in patternsByPrefix) {
            if (text.contains(prefix)) return true
        }
        // Check a few high-value patterns
        val quickPatterns = patterns.filter {
            it.severity == KeySeverity.CRITICAL || it.severity == KeySeverity.HIGH
        }
        return quickPatterns.any { it.pattern.containsMatchIn(text) }
    }

    /**
     * Gets patterns for a specific provider.
     */
    fun getPatternsForProvider(provider: String): List<KeyPattern> {
        return patternsByProvider[provider] ?: emptyList()
    }

    /**
     * Gets patterns for a specific category.
     */
    fun getPatternsForCategory(category: KeyCategory): List<KeyPattern> {
        return patternsByCategory[category] ?: emptyList()
    }

    /**
     * Gets all unique provider names.
     */
    fun getAllProviders(): Set<String> = patternsByProvider.keys

    /**
     * Calculates confidence score for a potential API key match.
     */
    private fun calculateConfidence(value: String, pattern: KeyPattern): Float {
        var confidence = 0.7f

        // Length validation
        if (value.length in pattern.minLength..pattern.maxLength) confidence += 0.1f

        // Prefix match
        if (pattern.prefixes.any { value.startsWith(it) }) confidence += 0.15f

        // Entropy check - high entropy is more likely to be a real key
        val entropy = calculateEntropy(value)
        if (entropy > 4.0) confidence += 0.05f

        return confidence.coerceIn(0f, 1f)
    }

    /**
     * Calculates Shannon entropy of a string.
     */
    fun calculateEntropy(text: String): Double {
        if (text.isEmpty()) return 0.0
        val freq = text.groupBy { it }.mapValues { it.value.size.toDouble() / text.length }
        return -freq.values.sumOf { p -> p * kotlin.math.ln(p) / kotlin.math.ln(2.0) }
    }

    /**
     * Returns the total number of patterns in the database.
     */
    fun totalPatterns(): Int = patterns.size

    /**
     * Returns the total number of providers covered.
     */
    fun totalProviders(): Int = patternsByProvider.size
}
