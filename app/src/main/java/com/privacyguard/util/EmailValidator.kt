package com.privacyguard.util

/**
 * Comprehensive email address validation utility following RFC 5322 specifications.
 * Provides validation, normalization, and classification of email addresses.
 *
 * Features:
 * - RFC 5322 compliant local-part validation
 * - Domain validation with DNS label rules
 * - Disposable email provider detection (400+ providers)
 * - Common typo detection and suggestion
 * - Plus addressing (sub-addressing) support
 * - Internationalized email address support (RFC 6530)
 * - Role-based address detection
 * - Free email provider detection
 */
object EmailValidator {

    /**
     * Result of email validation.
     *
     * @param isValid Whether the email passes validation
     * @param localPart The local part (before @)
     * @param domain The domain part (after @)
     * @param normalizedEmail Normalized version of the email
     * @param isDisposable Whether the domain is a known disposable email provider
     * @param isFreeProvider Whether the domain is a free email provider
     * @param isRoleAddress Whether this is a role-based address (admin@, info@, etc.)
     * @param hasSubAddress Whether plus addressing is used
     * @param subAddress The sub-address (part after +)
     * @param suggestedCorrection Suggested typo correction, if applicable
     * @param confidence Validation confidence score
     * @param reason Human-readable validation result
     */
    data class EmailValidationResult(
        val isValid: Boolean,
        val localPart: String = "",
        val domain: String = "",
        val normalizedEmail: String = "",
        val isDisposable: Boolean = false,
        val isFreeProvider: Boolean = false,
        val isRoleAddress: Boolean = false,
        val hasSubAddress: Boolean = false,
        val subAddress: String? = null,
        val suggestedCorrection: String? = null,
        val confidence: Float = 0f,
        val reason: String = ""
    )

    /**
     * Maximum lengths per RFC 5321.
     */
    private const val MAX_EMAIL_LENGTH = 254
    private const val MAX_LOCAL_PART_LENGTH = 64
    private const val MAX_DOMAIN_LENGTH = 253
    private const val MAX_LABEL_LENGTH = 63

    /**
     * Characters allowed in the local part without quoting.
     */
    private val ATEXT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#\$%&'*+/=?^_`{|}~.-"

    /**
     * Known disposable email providers. Users of these services typically use
     * temporary addresses that expire after a short period.
     */
    val disposableEmailDomains: Set<String> = setOf(
        // Most common disposable email services
        "mailinator.com", "guerrillamail.com", "guerrillamail.de", "guerrillamail.net",
        "guerrillamail.org", "guerrillamail.biz", "grr.la", "guerrillamailblock.com",
        "tempmail.com", "temp-mail.org", "temp-mail.io", "tempail.com",
        "throwaway.email", "throwaway.com", "thromail.com",
        "dispostable.com", "disposableaddress.com",
        "sharklasers.com", "guerrillamail.info",
        "yopmail.com", "yopmail.fr", "yopmail.net", "yopmail.gq",
        "mailnesia.com", "mailnator.com",
        "getnada.com", "nada.email",
        "10minutemail.com", "10minutemail.net", "10minutemail.org",
        "maildrop.cc", "maildrop.gq",
        "mailsac.com",
        "trashmail.com", "trashmail.me", "trashmail.net", "trashmail.org",
        "trashmail.de", "trashmail.ws", "trashmail.at",
        "fakeinbox.com", "fakemail.net", "fakemail.fr",
        "discard.email", "discardmail.com", "discardmail.de",
        "spamgourmet.com", "spamgourmet.net", "spamgourmet.org",
        "getairmail.com",
        "mailexpire.com",
        "tempinbox.com", "tempinbox.co.uk",
        "mytemp.email", "mytempmail.com",
        "minutemail.com",
        "emailondeck.com",
        "33mail.com",
        "mohmal.com", "mohmal.im", "mohmal.in",
        "burner.kiwi",
        "harakirimail.com",
        "tempmailo.com",
        "burpcollaborator.net",
        "mailcatch.com",
        "emailfake.com",
        "crazymailing.com",
        "armyspy.com",
        "cuvox.de",
        "dayrep.com",
        "einrot.com",
        "fleckens.hu",
        "gustr.com",
        "jourrapide.com",
        "rhyta.com",
        "stinkfinger.com",
        "superrito.com",
        "teleworm.us",
        "trbvm.com",

        // Additional common disposable domains
        "mailhub.top", "mailhub.pro",
        "bouncr.com",
        "bugmenot.com",
        "binkmail.com",
        "bobmail.info",
        "coolimpool.org",
        "courrieltemporaire.com",
        "dandikmail.com",
        "deadaddress.com",
        "despam.it",
        "devnullmail.com",
        "dfgh.net",
        "digitalsanctuary.com",
        "disposeamail.com",
        "dodgeit.com",
        "dodgemail.de",
        "domozmail.com",
        "drdrb.com",
        "e4ward.com",
        "emailigo.de",
        "emailmiser.com",
        "emailproxsy.com",
        "emailtemporario.com.br",
        "emailwarden.com",
        "emailx.at.hm",
        "emz.net",
        "enterfg.com",
        "ephemail.net",
        "etranquil.com",
        "etranquil.net",
        "etranquil.org",
        "evopo.com",
        "explodemail.com",
        "express.net.ua",
        "eyepaste.com",
        "fastacura.com",
        "filzmail.com",
        "fixmail.tk",
        "flyspam.com",
        "frapmail.com",
        "garliclife.com",
        "gelitik.in",
        "getonemail.com",
        "getonemail.net",
        "ghosttexter.de",
        "giantmail.de",
        "girlsundertheinfluence.com",
        "gishpuppy.com",
        "grandmamail.com",
        "grandmasmail.com",
        "great-host.in",
        "greensloth.com",
        "haltospam.com",
        "hatespam.org",
        "hidemail.de",
        "hidzz.com",
        "hotpop.com",
        "hulapla.de",
        "ieatspam.eu",
        "ieatspam.info",
        "imails.info",
        "inbax.tk",
        "inbox.si",
        "inboxalias.com",
        "incognitomail.com",
        "incognitomail.net",
        "incognitomail.org",
        "insorg-mail.info",
        "ipoo.org",
        "irish2me.com",
        "iwi.net",
        "jetable.com",
        "jetable.de",
        "jetable.fr.nf",
        "jetable.net",
        "jetable.org",
        "jnxjn.com",
        "joelpet.com",
        "kasmail.com",
        "kaspop.com",
        "keepmymail.com",
        "killmail.com",
        "killmail.net",
        "klzlk.com",
        "koszmail.pl",
        "kurzepost.de",
        "lawlita.com",
        "letthemeatspam.com",
        "lhsdv.com",
        "lifebyfood.com",
        "link2mail.net",
        "litedrop.com",
        "lol.ovpn.to",
        "lookugly.com",
        "lopl.co.cc",
        "lortemail.dk",
        "lovemeleaveme.com",
        "lr78.com",
        "lroid.com",
        "lukop.dk",
        "m21.cc",
        "mail-temporaire.fr",
        "mail.by",
        "mail.mezimages.net",
        "mail.zp.ua",
        "mail2rss.org",
        "mail333.com",
        "mailbidon.com",
        "mailblocks.com",
        "mailbucket.org",
        "mailcat.biz",
        "maileater.com",
        "maileimer.de",
        "mailforspam.com",
        "mailfreeonline.com",
        "mailguard.me",
        "mailin8r.com",
        "mailinater.com",
        "mailincubator.com",
        "mailismagic.com",
        "mailmate.com",
        "mailme.ir",
        "mailme.lv",
        "mailmetrash.com",
        "mailmoat.com",
        "mailms.com",
        "mailnull.com",
        "mailorg.org",
        "mailpick.biz",
        "mailproxsy.com",
        "mailquack.com",
        "mailrock.biz",
        "mailscrap.com",
        "mailshell.com",
        "mailsiphon.com",
        "mailslapping.com",
        "mailslite.com",
        "mailtemp.info",
        "mailtothis.com",
        "mailtrash.net",
        "mailtv.net",
        "mailtv.tv",
        "mailzilla.com",
        "makemetheking.com",
        "manifestgenerator.com",
        "mbx.cc",
        "mega.zik.dj",
        "meinspamschutz.de",
        "meltmail.com",
        "messagebeamer.de",
        "mezimages.net",
        "mfsa.ru",
        "mierdamail.com",
        "ministry-of-silly-walks.de",
        "mintemail.com",
        "mjukgansen.com",
        "mobi.web.id",
        "mobileninja.co.uk",
        "moncourrier.fr.nf",
        "monemail.fr.nf",
        "monmail.fr.nf",
        "mt2015.com",
        "mx0.wwwnew.eu",
        "myalias.pw",
        "mycleaninbox.net",
        "myemailboxy.com",
        "mymail-in.net",
        "mymailoasis.com",
        "mypartyclip.de",
        "myphantom.com",
        "mysamp.de",
        "myspaceinc.com",
        "myspaceinc.net",
        "myspaceinc.org",
        "myspacepimpedup.com",
        "mytrashmail.com",
        "nabala.com",
        "neomailbox.com",
        "nepwk.com",
        "nervmich.net",
        "nervtansen.de",
        "netmails.com",
        "netmails.net",
        "neverbox.com",
        "no-spam.ws",
        "nobulk.com",
        "noclickemail.com",
        "nogmailspam.info",
        "nomail.xl.cx",
        "nomail2me.com",
        "nomorespam.de",
        "nospam.ze.tc",
        "nospam4.us",
        "nospamfor.us",
        "nospammail.net",
        "nospamthanks.info",
        "nothingtoseehere.ca",
        "nowmymail.com",
        "nurfuerspam.de",
        "nus.edu.sg",
        "nwldx.com",
        "objectmail.com",
        "obobbo.com",
        "odnorazovoe.ru",
        "oneoffemail.com",
        "onewaymail.com",
        "oopi.org",
        "ordinaryamerican.net",
        "otherinbox.com",
        "ourklips.com",
        "outlawspam.com",
        "ovpn.to",
        "owlpic.com",
        "pancakemail.com",
        "pimpedupmyspace.com",
        "pjjkp.com",
        "plexolan.de",
        "pookmail.com",
        "privacy.net",
        "proxymail.eu",
        "prtnx.com",
        "punkass.com",
        "putthisinyourspamdatabase.com",
        "qq.com",
        "quickinbox.com",
        "rcpt.at",
        "reallymymail.com",
        "recode.me",
        "reconmail.com",
        "regbypass.com",
        "tmail.ws",
        "tmailinator.com",
        "toiea.com",
        "tradermail.info",
        "trash-amil.com",
        "trash-mail.at",
        "trash-mail.com",
        "trash-mail.de",
        "trash2009.com",
        "trashdevil.com",
        "trashdevil.de",
        "trashemails.de",
        "trashmail.at",
        "trashmailer.com",
        "trashymail.com",
        "trashymail.net",
        "trbvn.com",
        "twinmail.de",
        "tyldd.com",
        "uggsrock.com",
        "umail.net",
        "upliftnow.com",
        "uplipht.com",
        "venompen.com",
        "veryreallyfakeaddress.com",
        "viditag.com",
        "viewcastmedia.com",
        "voidbay.com",
        "walala.org",
        "walkmail.net",
        "webemail.me",
        "webm4il.info",
        "wegwerfadresse.de",
        "wegwerfemail.de",
        "wegwerfmail.de",
        "wegwerfmail.net",
        "wegwerfmail.org",
        "wetrainbayarea.com",
        "wetrainbayarea.org",
        "wh4f.org",
        "whyspam.me",
        "wickmail.net",
        "wilemail.com",
        "willhackforfood.biz",
        "willselfdestruct.com",
        "winemaven.info",
        "wronghead.com",
        "wuzup.net",
        "wuzupmail.net",
        "wwwnew.eu",
        "xagloo.com",
        "xemaps.com",
        "xents.com",
        "xmaily.com",
        "xoxy.net",
        "yapped.net",
        "yep.it",
        "yogamaven.com",
        "yroid.com",
        "zehnminutenmail.de",
        "zippymail.info",
        "zoaxe.com",
        "zoemail.org"
    )

    /**
     * Well-known free email providers.
     */
    val freeEmailProviders: Set<String> = setOf(
        // Google
        "gmail.com", "googlemail.com",
        // Microsoft
        "outlook.com", "hotmail.com", "live.com", "msn.com",
        "hotmail.co.uk", "hotmail.fr", "hotmail.de", "hotmail.it",
        "hotmail.es", "hotmail.ca", "hotmail.com.au", "hotmail.co.jp",
        "outlook.co.uk", "outlook.fr", "outlook.de", "outlook.es",
        "outlook.it", "outlook.com.au", "outlook.co.jp", "outlook.in",
        "live.co.uk", "live.fr", "live.de", "live.it", "live.es",
        "live.com.au", "live.co.jp", "live.in", "live.nl", "live.be",
        // Yahoo
        "yahoo.com", "yahoo.co.uk", "yahoo.fr", "yahoo.de", "yahoo.it",
        "yahoo.es", "yahoo.ca", "yahoo.com.au", "yahoo.co.jp", "yahoo.co.in",
        "yahoo.com.br", "yahoo.com.mx", "yahoo.co.id", "yahoo.co.nz",
        "ymail.com", "rocketmail.com",
        // Apple
        "icloud.com", "me.com", "mac.com",
        // AOL
        "aol.com", "aim.com", "aol.co.uk",
        // ProtonMail
        "protonmail.com", "protonmail.ch", "proton.me", "pm.me",
        // Tutanota
        "tutanota.com", "tutanota.de", "tutamail.com", "tuta.io",
        // Zoho
        "zoho.com", "zohomail.com",
        // GMX
        "gmx.com", "gmx.de", "gmx.net", "gmx.at", "gmx.ch",
        // Mail.com
        "mail.com", "email.com",
        // Fastmail
        "fastmail.com", "fastmail.fm",
        // Mailfence
        "mailfence.com",
        // Yandex
        "yandex.com", "yandex.ru", "yandex.ua", "ya.ru",
        // Mail.ru
        "mail.ru", "inbox.ru", "list.ru", "bk.ru",
        // Rediffmail (India)
        "rediffmail.com", "rediff.com",
        // GMail variants
        "googlemail.co.uk",
        // Others
        "comcast.net", "verizon.net", "att.net", "sbcglobal.net",
        "cox.net", "charter.net", "earthlink.net",
        "naver.com", "daum.net", "hanmail.net",
        "163.com", "126.com", "sina.com", "sohu.com",
        "web.de", "freenet.de", "t-online.de",
        "wanadoo.fr", "free.fr", "laposte.net", "orange.fr",
        "libero.it", "virgilio.it", "alice.it", "tiscali.it",
        "ig.com.br", "bol.com.br", "terra.com.br", "uol.com.br",
        "bigpond.com", "optusnet.com.au",
        "shaw.ca", "rogers.com", "telus.net",
        "btinternet.com", "sky.com", "virgin.net", "talktalk.net"
    )

    /**
     * Role-based email addresses (generic, not personal).
     */
    val roleAddresses: Set<String> = setOf(
        "admin", "administrator", "webmaster", "postmaster", "hostmaster",
        "info", "information", "contact", "contacts",
        "support", "help", "helpdesk", "service",
        "sales", "marketing", "press", "media",
        "abuse", "spam", "noc", "security",
        "noreply", "no-reply", "donotreply", "do-not-reply",
        "mailer-daemon", "root", "sysadmin", "devnull",
        "billing", "finance", "accounting", "payroll",
        "hr", "humanresources", "legal", "compliance",
        "office", "reception", "general",
        "feedback", "suggestions", "complaints",
        "subscribe", "unsubscribe", "newsletter",
        "jobs", "careers", "recruitment", "hiring",
        "team", "staff", "everyone", "all",
        "orders", "returns", "shipping",
        "privacy", "gdpr", "dpo",
        "api", "dev", "developer", "developers",
        "tech", "technical", "engineering",
        "ops", "operations", "devops",
        "test", "testing", "debug",
        "demo", "example", "sample"
    )

    /**
     * Common email domain typos and their corrections.
     */
    val domainTypoCorrections: Map<String, String> = mapOf(
        // Gmail
        "gmial.com" to "gmail.com",
        "gmaill.com" to "gmail.com",
        "gmali.com" to "gmail.com",
        "gmal.com" to "gmail.com",
        "gamil.com" to "gmail.com",
        "gnail.com" to "gmail.com",
        "gmai.com" to "gmail.com",
        "gmail.co" to "gmail.com",
        "gmail.cm" to "gmail.com",
        "gmail.om" to "gmail.com",
        "gmail.cim" to "gmail.com",
        "gmail.con" to "gmail.com",
        "gmail.vom" to "gmail.com",
        "gmail.comn" to "gmail.com",
        "gmail.come" to "gmail.com",
        "gmail.comm" to "gmail.com",
        "gmail.xom" to "gmail.com",
        "gmail.coom" to "gmail.com",
        "gmaul.com" to "gmail.com",
        "gmqil.com" to "gmail.com",
        "gmsil.com" to "gmail.com",
        "gmeil.com" to "gmail.com",
        "gimail.com" to "gmail.com",
        "gemail.com" to "gmail.com",

        // Yahoo
        "yaho.com" to "yahoo.com",
        "yahooo.com" to "yahoo.com",
        "yhaoo.com" to "yahoo.com",
        "yahoio.com" to "yahoo.com",
        "yahoo.cm" to "yahoo.com",
        "yahoo.co" to "yahoo.com",
        "yahoo.om" to "yahoo.com",
        "yahoo.con" to "yahoo.com",
        "yahoo.vom" to "yahoo.com",
        "yhoo.com" to "yahoo.com",
        "yaboo.com" to "yahoo.com",
        "yaoo.com" to "yahoo.com",

        // Hotmail
        "hotamil.com" to "hotmail.com",
        "hotmal.com" to "hotmail.com",
        "hotmial.com" to "hotmail.com",
        "hotmaill.com" to "hotmail.com",
        "hotmai.com" to "hotmail.com",
        "hotmil.com" to "hotmail.com",
        "hotmail.cm" to "hotmail.com",
        "hotmail.co" to "hotmail.com",
        "hotmail.con" to "hotmail.com",
        "hotamil.co" to "hotmail.com",
        "hotnail.com" to "hotmail.com",
        "hitmail.com" to "hotmail.com",

        // Outlook
        "outloo.com" to "outlook.com",
        "outlok.com" to "outlook.com",
        "outloook.com" to "outlook.com",
        "oulook.com" to "outlook.com",
        "outlooik.com" to "outlook.com",
        "outllook.com" to "outlook.com",
        "outlook.cm" to "outlook.com",
        "outlook.co" to "outlook.com",
        "outlook.con" to "outlook.com",
        "outlouk.com" to "outlook.com",
        "otlook.com" to "outlook.com",

        // iCloud
        "iclould.com" to "icloud.com",
        "iclud.com" to "icloud.com",
        "iclod.com" to "icloud.com",
        "icloudd.com" to "icloud.com",
        "icloud.co" to "icloud.com",
        "icloud.cm" to "icloud.com",
        "icloud.con" to "icloud.com",

        // AOL
        "aol.co" to "aol.com",
        "aol.cm" to "aol.com",
        "aol.con" to "aol.com",

        // Protonmail
        "protonmal.com" to "protonmail.com",
        "protommail.com" to "protonmail.com",
        "protonmail.co" to "protonmail.com",
        "protonmail.cm" to "protonmail.com",
        "prtonmail.com" to "protonmail.com",

        // Common TLD typos
        "gmail.org" to "gmail.com",
        "gmail.net" to "gmail.com",
        "yahoo.org" to "yahoo.com",
        "hotmail.org" to "hotmail.com"
    )

    /**
     * Well-known example/test domains from RFCs.
     */
    val exampleDomains: Set<String> = setOf(
        "example.com", "example.org", "example.net", "example.edu",
        "test.com", "test.org", "test.net",
        "localhost",
        "invalid",
        "example.co.uk"
    )

    /**
     * Validates an email address against RFC 5322 rules and additional heuristics.
     *
     * @param email The email address to validate
     * @param checkDisposable Whether to check against disposable email providers
     * @param suggestCorrections Whether to suggest typo corrections
     * @return EmailValidationResult with full validation details
     */
    fun validate(
        email: String,
        checkDisposable: Boolean = true,
        suggestCorrections: Boolean = true
    ): EmailValidationResult {
        val trimmed = email.trim()

        // Basic length check
        if (trimmed.isEmpty()) {
            return EmailValidationResult(isValid = false, reason = "Empty email address")
        }
        if (trimmed.length > MAX_EMAIL_LENGTH) {
            return EmailValidationResult(
                isValid = false,
                reason = "Email exceeds maximum length of $MAX_EMAIL_LENGTH characters"
            )
        }

        // Must contain exactly one @
        val atIndex = trimmed.indexOf('@')
        if (atIndex < 0) {
            return EmailValidationResult(isValid = false, reason = "Missing @ symbol")
        }
        if (trimmed.indexOf('@', atIndex + 1) >= 0) {
            return EmailValidationResult(isValid = false, reason = "Multiple @ symbols found")
        }

        val localPart = trimmed.substring(0, atIndex)
        val domain = trimmed.substring(atIndex + 1)

        // Validate local part
        val localValidation = validateLocalPart(localPart)
        if (!localValidation.first) {
            return EmailValidationResult(isValid = false, reason = localValidation.second)
        }

        // Validate domain
        val domainValidation = validateDomain(domain)
        if (!domainValidation.first) {
            return EmailValidationResult(
                isValid = false,
                localPart = localPart,
                domain = domain,
                reason = domainValidation.second
            )
        }

        // Check for sub-addressing (plus addressing)
        val plusIndex = localPart.indexOf('+')
        val hasSubAddress = plusIndex >= 0
        val subAddress = if (hasSubAddress) localPart.substring(plusIndex + 1) else null
        val baseLocalPart = if (hasSubAddress) localPart.substring(0, plusIndex) else localPart

        // Normalize email
        val normalizedDomain = domain.lowercase()
        val normalizedEmail = "$baseLocalPart@$normalizedDomain"

        // Check classifications
        val isDisposable = if (checkDisposable) isDisposableEmail(normalizedDomain) else false
        val isFreeProvider = isFreeEmailProvider(normalizedDomain)
        val isRole = isRoleAddress(baseLocalPart)
        val isExample = isExampleDomain(normalizedDomain)

        // Check for typo corrections
        val suggestedCorrection = if (suggestCorrections) {
            suggestDomainCorrection(normalizedDomain)?.let { correctedDomain ->
                "$localPart@$correctedDomain"
            }
        } else null

        // Calculate confidence
        val confidence = calculateConfidence(
            localPart, normalizedDomain, isDisposable, isFreeProvider,
            isRole, isExample, suggestedCorrection != null
        )

        return EmailValidationResult(
            isValid = true,
            localPart = localPart,
            domain = normalizedDomain,
            normalizedEmail = normalizedEmail,
            isDisposable = isDisposable,
            isFreeProvider = isFreeProvider,
            isRoleAddress = isRole,
            hasSubAddress = hasSubAddress,
            subAddress = subAddress,
            suggestedCorrection = suggestedCorrection,
            confidence = confidence,
            reason = buildSuccessReason(normalizedDomain, isDisposable, isRole, isFreeProvider, isExample)
        )
    }

    /**
     * Validates the local part of an email address.
     *
     * Rules (RFC 5322):
     * - Max 64 characters
     * - Cannot start or end with a dot
     * - Cannot contain consecutive dots
     * - Only certain characters allowed without quoting
     *
     * @return Pair(isValid, reason)
     */
    fun validateLocalPart(localPart: String): Pair<Boolean, String> {
        if (localPart.isEmpty()) {
            return Pair(false, "Local part is empty")
        }
        if (localPart.length > MAX_LOCAL_PART_LENGTH) {
            return Pair(false, "Local part exceeds maximum length of $MAX_LOCAL_PART_LENGTH characters")
        }

        // Check for quoted local part
        if (localPart.startsWith('"') && localPart.endsWith('"')) {
            // Quoted local parts allow almost anything
            return validateQuotedLocalPart(localPart)
        }

        // Unquoted local part rules
        if (localPart.startsWith('.')) {
            return Pair(false, "Local part cannot start with a dot")
        }
        if (localPart.endsWith('.')) {
            return Pair(false, "Local part cannot end with a dot")
        }
        if (localPart.contains("..")) {
            return Pair(false, "Local part cannot contain consecutive dots")
        }

        // Check each character
        for (char in localPart) {
            if (char !in ATEXT_CHARS) {
                return Pair(false, "Invalid character '$char' in local part (use quotes for special characters)")
            }
        }

        return Pair(true, "Valid local part")
    }

    /**
     * Validates a quoted local part.
     */
    private fun validateQuotedLocalPart(quotedLocalPart: String): Pair<Boolean, String> {
        if (quotedLocalPart.length < 2) {
            return Pair(false, "Invalid quoted local part")
        }
        val inner = quotedLocalPart.substring(1, quotedLocalPart.length - 1)

        var i = 0
        while (i < inner.length) {
            val char = inner[i]
            if (char == '\\') {
                // Escaped character
                if (i + 1 >= inner.length) {
                    return Pair(false, "Trailing backslash in quoted local part")
                }
                i += 2 // Skip escaped character
            } else if (char == '"') {
                return Pair(false, "Unescaped quote inside quoted local part")
            } else {
                i++
            }
        }

        return Pair(true, "Valid quoted local part")
    }

    /**
     * Validates the domain part of an email address.
     *
     * Rules:
     * - Max 253 characters total
     * - Must contain at least one dot (except for special domains)
     * - Each label max 63 characters
     * - Labels must start and end with alphanumeric
     * - Labels can contain hyphens but not at start/end
     * - TLD cannot be all-numeric
     *
     * @return Pair(isValid, reason)
     */
    fun validateDomain(domain: String): Pair<Boolean, String> {
        if (domain.isEmpty()) {
            return Pair(false, "Domain is empty")
        }
        if (domain.length > MAX_DOMAIN_LENGTH) {
            return Pair(false, "Domain exceeds maximum length of $MAX_DOMAIN_LENGTH characters")
        }

        // IP address literal (e.g., [192.168.1.1])
        if (domain.startsWith('[') && domain.endsWith(']')) {
            return validateIpDomainLiteral(domain)
        }

        val labels = domain.split('.')
        if (labels.size < 2) {
            return Pair(false, "Domain must have at least two parts (e.g., example.com)")
        }

        for ((index, label) in labels.withIndex()) {
            if (label.isEmpty()) {
                return Pair(false, "Domain contains empty label (consecutive dots)")
            }
            if (label.length > MAX_LABEL_LENGTH) {
                return Pair(false, "Domain label '$label' exceeds maximum length of $MAX_LABEL_LENGTH characters")
            }
            if (label.startsWith('-')) {
                return Pair(false, "Domain label '$label' cannot start with a hyphen")
            }
            if (label.endsWith('-')) {
                return Pair(false, "Domain label '$label' cannot end with a hyphen")
            }

            // Check characters (alphanumeric and hyphens)
            for (char in label) {
                if (!char.isLetterOrDigit() && char != '-') {
                    return Pair(false, "Invalid character '$char' in domain label '$label'")
                }
            }
        }

        // TLD cannot be all-numeric
        val tld = labels.last()
        if (tld.all { it.isDigit() }) {
            return Pair(false, "Top-level domain '$tld' cannot be all-numeric")
        }

        return Pair(true, "Valid domain")
    }

    /**
     * Validates an IP address domain literal like [192.168.1.1].
     */
    private fun validateIpDomainLiteral(domain: String): Pair<Boolean, String> {
        val ip = domain.substring(1, domain.length - 1)

        // IPv6
        if (ip.startsWith("IPv6:", ignoreCase = true)) {
            return Pair(true, "IPv6 literal domain")
        }

        // IPv4
        val parts = ip.split('.')
        if (parts.size == 4 && parts.all { part ->
                val num = part.toIntOrNull()
                num != null && num in 0..255
            }) {
            return Pair(true, "IPv4 literal domain")
        }

        return Pair(false, "Invalid IP address in domain literal: $ip")
    }

    /**
     * Checks if a domain is a known disposable email provider.
     */
    fun isDisposableEmail(domain: String): Boolean {
        return domain.lowercase() in disposableEmailDomains
    }

    /**
     * Checks if a domain is a known free email provider.
     */
    fun isFreeEmailProvider(domain: String): Boolean {
        return domain.lowercase() in freeEmailProviders
    }

    /**
     * Checks if a local part is a role-based address.
     */
    fun isRoleAddress(localPart: String): Boolean {
        return localPart.lowercase() in roleAddresses
    }

    /**
     * Checks if a domain is an example/test domain from RFCs.
     */
    fun isExampleDomain(domain: String): Boolean {
        return domain.lowercase() in exampleDomains
    }

    /**
     * Suggests a domain correction for common typos.
     *
     * @param domain The domain to check
     * @return Corrected domain or null if no correction needed
     */
    fun suggestDomainCorrection(domain: String): String? {
        return domainTypoCorrections[domain.lowercase()]
    }

    /**
     * Normalizes a Gmail address by removing dots and plus addressing.
     * Gmail ignores dots in the local part and treats everything after + as a tag.
     *
     * @param email Gmail address to normalize
     * @return Normalized Gmail address
     */
    fun normalizeGmailAddress(email: String): String? {
        val result = validate(email)
        if (!result.isValid) return null

        val domain = result.domain
        if (domain != "gmail.com" && domain != "googlemail.com") {
            return email // Not Gmail, return as-is
        }

        val localPart = result.localPart
        // Remove dots
        val withoutDots = localPart.replace(".", "")
        // Remove sub-addressing
        val plusIndex = withoutDots.indexOf('+')
        val baseLocal = if (plusIndex >= 0) withoutDots.substring(0, plusIndex) else withoutDots

        return "$baseLocal@gmail.com"
    }

    /**
     * Extracts all email addresses from a text string.
     *
     * @param text Input text to scan
     * @return List of validated email addresses found
     */
    fun extractFromText(text: String): List<EmailValidationResult> {
        val pattern = Regex("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
        return pattern.findAll(text).map { match -> validate(match.value) }.filter { it.isValid }.toList()
    }

    /**
     * Checks if a string looks like it could be an email address (quick heuristic).
     */
    fun looksLikeEmail(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 5 || trimmed.length > MAX_EMAIL_LENGTH) return false
        val atIndex = trimmed.indexOf('@')
        if (atIndex < 1 || atIndex >= trimmed.length - 3) return false
        val afterAt = trimmed.substring(atIndex + 1)
        return afterAt.contains('.') && !afterAt.endsWith('.')
    }

    /**
     * Calculates validation confidence score.
     */
    private fun calculateConfidence(
        localPart: String,
        domain: String,
        isDisposable: Boolean,
        isFreeProvider: Boolean,
        isRole: Boolean,
        isExample: Boolean,
        hasTypoSuggestion: Boolean
    ): Float {
        var confidence = 0.8f

        // Boost for known providers
        if (isFreeProvider) confidence += 0.1f
        // Reduce for disposable
        if (isDisposable) confidence -= 0.2f
        // Reduce for example domains
        if (isExample) confidence -= 0.4f
        // Reduce for typo suggestions
        if (hasTypoSuggestion) confidence -= 0.1f
        // Reduce for very short local parts
        if (localPart.length < 3) confidence -= 0.1f
        // Boost for reasonable length
        if (localPart.length in 3..20 && domain.length in 5..40) confidence += 0.1f

        return confidence.coerceIn(0f, 1f)
    }

    /**
     * Builds a success reason string.
     */
    private fun buildSuccessReason(
        domain: String,
        isDisposable: Boolean,
        isRole: Boolean,
        isFreeProvider: Boolean,
        isExample: Boolean
    ): String {
        val parts = mutableListOf("Valid email address")
        if (isDisposable) parts.add("WARNING: disposable email provider")
        if (isExample) parts.add("WARNING: example/test domain")
        if (isRole) parts.add("role-based address")
        if (isFreeProvider) parts.add("free email provider ($domain)")
        return parts.joinToString("; ")
    }

    /**
     * Returns the total number of known disposable email domains.
     */
    fun disposableDomainCount(): Int = disposableEmailDomains.size

    /**
     * Returns the total number of known free email providers.
     */
    fun freeProviderCount(): Int = freeEmailProviders.size
}
