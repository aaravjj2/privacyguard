package com.privacyguard.testutil

/**
 * A large database of sample text strings for testing PII detection in natural-language contexts.
 * All names, numbers, and personal details are entirely synthetic.
 */
object PIISampleTextDatabase {

    // =========================================================================
    // Medical / Healthcare Context Texts
    // =========================================================================

    val MEDICAL_TEXTS_WITH_SSN = listOf(
        "Patient John Smith (SSN: 245-12-3456) admitted on 03/15/2024 for routine checkup.",
        "Insurance claim for Mary Johnson, SSN 312-45-6789, Date of Birth: June 4, 1978.",
        "Please verify patient SSN 401-23-5678 against records before processing prescription.",
        "HIPAA record for SSN: 555-67-8901 — diabetic management plan, reviewed 01/10/2024.",
        "Emergency contact form: patient SSN 123-45-6789, next of kin: Jane Doe, (555) 234-5678.",
        "Pre-authorization required for SSN 288-34-5678; prior auth code: PA-12345.",
        "Lab results pending for patient 301-56-7890; call back number for physician: 800-555-0111.",
        "Discharge summary: SSN 400-78-9012, admitted 02/01, discharged 02/05, diagnosis: pneumonia.",
        "Medicare claim submitted for SSN 350-90-1234; claim number: MCR-2024-0012345.",
        "Patient consent form signed; SSN 275-12-3456, procedure: appendectomy, surgeon: Dr. Adams.",
        "Re-admission note: SSN 198-34-5678 returned with recurring symptoms, 14 days post-discharge.",
        "Rx for SSN 437-56-7890: metformin 500mg twice daily, 90-day supply, refills: 3.",
        "Radiology order for patient 501-78-9012: CT chest with contrast, payer: BlueCross.",
        "Allergy alert on file: SSN 262-90-1234 — penicillin (anaphylaxis), latex (contact).",
        "Dietitian consult requested: SSN 388-12-3456, BMI 34.2, goal: 10% weight reduction.",
        "Physical therapy referral: SSN 471-34-5678, diagnosis: rotator cuff tear, 12 sessions.",
        "COVID-19 vaccine record: SSN 153-56-7890, Lot: EL9261, dose 1 on 01/25/2021.",
        "Mental health referral: SSN 244-78-9012, GAD assessment score: 14 (moderate).",
        "Prior authorization approved: SSN 377-90-1234, Humira 40mg/0.8mL biweekly.",
        "Surgical consent for SSN 499-12-3456: bilateral knee replacement, anesthesia: general.",
        "Pathology report for SSN 210-34-5678: colon biopsy negative for malignancy.",
        "Cardiology follow-up: SSN 342-56-7890, EF 55%, BP 128/82, no medication changes.",
        "Home health order: SSN 421-78-9012, wound care for diabetic foot ulcer, 3x/week.",
        "FMLA paperwork for SSN 183-90-1234: chronic back pain, medical leave approved 6 weeks.",
        "Speech therapy evaluation: SSN 315-12-3456, post-stroke aphasia, 2x/week sessions.",
    )

    val MEDICAL_TEXTS_WITH_CARD = listOf(
        "Patient co-pay of \$35.00 charged to Visa 4111111111111111 on 03/15/2024.",
        "Insurance balance of \$120.50 billed to MasterCard 5500005555555559 on file.",
        "Pharmacy payment for \$78.99 processed on Amex 371449635398431.",
        "Lab invoice \$245.00 charged to card ending in 1111 (Visa 4111 1111 1111 1111).",
        "Pre-payment for elective procedure: \$500 deposit, card 4929415432944605 authorized.",
        "HSA card on file: Visa 4024007136512380 — used for prescription refill \$43.00.",
        "Annual physical co-pay \$0, preventive care. Deductible met. Card: 5425233430109903.",
        "ER visit balance \$892.40 — payment plan set up, monthly charge to 4916338506082832.",
        "Dental claim: \$1,240 for crown. Patient portion: \$310. Card: 4539578763621486.",
        "Optical benefit used. Frames + lenses: \$420. Card charged: 5200828282828210.",
    )

    val MEDICAL_TEXTS_NO_PII = listOf(
        "The new electronic health records system will be deployed across all three campuses next quarter.",
        "Staff reminder: all patient interactions must follow HIPAA minimum necessary standard.",
        "The pharmacy will be closed on Saturday for inventory — please plan refills accordingly.",
        "Clinical trial enrollment is now open for patients with Type 2 diabetes and elevated A1C.",
        "Hand hygiene compliance improved to 97% in the ICU this month — excellent work, team.",
        "New telehealth platform goes live Monday. Patients can access via web or iOS/Android app.",
        "Continuing education credits available for all nursing staff through the hospital learning portal.",
        "The cafeteria will serve extended hours on Thanksgiving Day for staff working the holiday shift.",
        "Reminder: flu vaccine clinic runs through November — all staff and volunteers are eligible.",
        "Quality improvement team meeting is Thursday at 2 PM in Conference Room B.",
    )

    // =========================================================================
    // Financial / Banking Context Texts
    // =========================================================================

    val FINANCIAL_TEXTS_WITH_CARD = listOf(
        "Your new Visa card ending in 4111 has been activated. Card number: 4111111111111111.",
        "Suspicious transaction detected on MasterCard 5500005555555559 — please verify.",
        "Wire transfer initiated from account linked to Visa 4111 1111 1111 1111, amount: \$12,500.",
        "Chargeback dispute filed for Amex 378282246310005, transaction \$340.00 on 02/14.",
        "Reward points redeemed on Discover card 6011111111111117 — applied \$50 statement credit.",
        "Card 5105105105105100 declined — insufficient funds, current balance: \$0.43.",
        "Contactless payment enabled for Visa 4012888888881881 via Google Pay.",
        "Card-not-present fraud alert: Amex 371449635398431 used for \$2,199 at foreign merchant.",
        "Balance transfer offer: 0% APR for 18 months on Visa 4916679762607382.",
        "Virtual card number generated: 4111 2345 6789 0123 (linked to primary Visa 4111111111111111).",
        "Earn 5x points on groceries with your Mastercard 5555555555554444 this month.",
        "Replacement card issued for JCB 3530111333300000 due to compromised data notification.",
        "Auto-pay enrollment confirmed for Discover 6011000990139424 — due date: 15th each month.",
        "Purchase protection claim filed: Visa 4539578763621486, item: laptop, value \$1,299.",
        "Annual fee of \$95 charged to card 4916338506082832 on your account anniversary.",
        "Travel notice added for card 5425233430109903 — destinations: France, Germany, Japan.",
        "ATM cash advance: \$200 withdrawn using Amex 341178571702187 at 08:23 PM.",
        "Chip & PIN transaction approved: Diners 30569309025904, merchant: Eurostar London.",
        "Interest charge: \$18.42 on outstanding balance, card 5200828282828210.",
        "Card benefits summary: Visa 4024007136512380 — rental insurance, extended warranty.",
    )

    val FINANCIAL_TEXTS_WITH_SSN = listOf(
        "Tax return filed for SSN 277-45-6789 — refund of \$1,234 processed via direct deposit.",
        "IRS audit notice issued to SSN 388-56-7890 for tax year 2022 — response required by 04/15.",
        "W-2 form generated for SSN 499-67-8901, employer: Acme Corp, wages: \$87,432.",
        "1099-INT sent to SSN 210-78-9012 for interest income: \$1,456.78 from savings account.",
        "401(k) contribution report: SSN 321-89-0123, contributions YTD: \$12,500, employer match: \$6,250.",
        "Social Security benefits statement: SSN 432-90-1234, projected benefit at age 67: \$2,340/mo.",
        "Student loan forgiveness application: SSN 543-01-2345, loan balance: \$34,500.",
        "Roth IRA contribution limit reached for SSN 154-12-3456 — maximum \$6,500 contributed.",
        "Mortgage application approved for SSN 265-23-4567, loan amount: \$425,000 at 6.875%.",
        "Background check consent: SSN 376-34-5678, purpose: employment screening, date: 03/01/2024.",
        "Child tax credit claim: SSN 487-45-6789, qualifying children: 2, credit amount: \$4,000.",
        "Bankruptcy filing: SSN 598-56-7890, Chapter 7, filing date: 02/28/2024.",
        "Auto loan application: SSN 209-67-8901, vehicle: 2024 Honda CR-V, loan: \$32,000.",
        "Credit freeze placed for SSN 320-78-9012 — effective immediately at all three bureaus.",
        "FAFSA submitted for SSN 431-89-0123, academic year 2024-2025, expected contribution: \$0.",
        "Life insurance policy: SSN 542-90-1234, coverage \$500,000, monthly premium \$78.",
        "Property tax exemption filed: SSN 153-01-2345, Clark County, homestead exemption approved.",
        "Alimony payment record: SSN 264-12-3456, court order WD-2023-11234, monthly \$1,800.",
        "VA loan certification: SSN 375-23-4567, entitlement: full, funding fee: 2.15%.",
        "Power of attorney registered: SSN 486-34-5678, agent: Robert Doe, scope: financial.",
    )

    val FINANCIAL_TEXTS_WITH_IBAN = listOf(
        "Wire transfer to DE89370400440532013000 (Deutsche Bank), amount EUR 5,000, ref: INV-2024-001.",
        "Payment received from GB29NWBK60161331926819 on 03/10/2024, amount £2,450.",
        "Invoice settled: NL91ABNA0417164300, EUR 1,234.56, payment date 03/15/2024.",
        "SEPA credit transfer initiated to FR7630006000011234567890189, amount EUR 890.",
        "Direct debit mandate authorized for BE68539007547034, monthly: EUR 299.",
        "Refund processed to SE4550000000058398257466, amount SEK 1,450.",
        "Supplier payment: IT60X0542811101000000123456, amount EUR 45,000, description: Q1 invoice.",
        "Cross-border payment: AT611904300234573201, CHF 3,200, correspondent bank: UBS.",
        "Account verification: CH9300762011623852957 — test deposit EUR 0.01 sent.",
        "Standing order created: ES9121000418450200051332, EUR 150/month, start date: 04/01.",
    )

    val FINANCIAL_TEXTS_NO_PII = listOf(
        "The Federal Reserve held rates steady at its March meeting, as expected by markets.",
        "Inflation slowed to 3.2% in February, the Bureau of Labor Statistics reported.",
        "Global equities rose today on positive earnings surprises from technology companies.",
        "The yield curve inverted further, with the 10-year Treasury falling to 4.12%.",
        "Oil prices rose 2% after OPEC+ announced extended production cuts through year-end.",
        "Consumer confidence improved in March, reaching the highest level in 18 months.",
        "The housing market showed signs of stabilizing as mortgage rates eased slightly.",
        "Tech sector earnings surprised to the upside, with AI infrastructure spend accelerating.",
        "The euro fell to a three-month low against the dollar on weak eurozone PMI data.",
        "Bitcoin surpassed \$70,000 for the first time, driven by institutional ETF inflows.",
    )

    // =========================================================================
    // Government / Legal Context Texts
    // =========================================================================

    val GOVERNMENT_TEXTS_WITH_SSN = listOf(
        "Unemployment claim submitted: SSN 233-12-3456, weekly benefit amount: \$478, effective 03/01.",
        "Social Security card replacement requested for SSN 344-23-4567, reason: lost.",
        "Disability benefit review: SSN 455-34-5678, scheduled interview 04/20/2024 at 9 AM.",
        "Veterans Affairs enrollment: SSN 566-45-6789, service dates: 2005-2009, branch: Army.",
        "Food assistance application: SSN 177-56-7890, household size: 3, monthly income: \$2,100.",
        "Medicaid enrollment: SSN 288-67-8901, coverage start: 03/01/2024, plan: Managed Care.",
        "Court-ordered child support: SSN 399-78-9012 (payor), amount: \$650/month.",
        "Naturalization certificate issued to SSN 410-89-0123 on 02/28/2024 in Orlando, FL.",
        "Voter registration: SSN (last 4 only — 3456), county: Travis, registered: 03/05/2024.",
        "State ID application: SSN 521-01-2345, photo ID issued, expiration: 2029.",
        "Department of Labor audit: SSN list of 15 employees requested. Employer: TechCorp LLC.",
        "SNAP recertification letter sent to SSN 132-12-3456, renewal deadline: 05/31/2024.",
        "EIDL loan application: SSN 243-23-4567, business: Main St Cafe, loan amount requested \$150K.",
        "DMV record transfer: SSN 354-34-5678, license surrendered in Ohio, new FL license issued.",
        "Passport applicant SSN: 465-45-6789, expiration: N/A (first passport), fee paid \$145.",
    )

    val LEGAL_TEXTS_WITH_EMAIL = listOf(
        "Please send executed contracts to legal@smithlaw.com by end of business Friday.",
        "The opposing counsel can be reached at j.doe@greenfieldpartners.com for all correspondence.",
        "Subpoena response should be directed to discovery@corporatelegal.org before the deadline.",
        "Plaintiff's expert witness can be contacted at dr.chen@forensicaccounting.edu.",
        "Filing confirmation sent to clerk@district7court.gov, case number 2024-CV-00123.",
        "Settlement offer communicated via email: opposing party's attorney at rbrown@litgroup.com.",
        "All mediation documents should go to mediation@conflictresolution.org.",
        "Notary public can be reached at notary.public@legalservices.net for witnessing.",
        "Court reporter transcript available at transcripts@courtreporting.com, case ID: TR-45678.",
        "Client portal login required for sensitive documents; contact support@legaltech.io.",
    )

    // =========================================================================
    // Employment / HR Context Texts
    // =========================================================================

    val HR_TEXTS_WITH_SSN = listOf(
        "New hire onboarding: SSN 277-34-5678 submitted for background check on 03/10/2024.",
        "Direct deposit setup for SSN 388-45-6789, routing: 121000248, account: 1234567890.",
        "W-4 withholding updated for SSN 499-56-7890 — married, 2 allowances.",
        "COBRA election deadline: SSN 210-67-8901 has 60 days from termination to enroll.",
        "Employee verification (I-9): SSN 321-78-9012, work authorization confirmed.",
        "Group health enrollment: SSN 432-89-0123, plan: PPO Plus, effective date: 04/01.",
        "Payroll garnishment received: SSN 543-90-1234, creditor: First National Bank, \$250/pay.",
        "Performance improvement plan: SSN 154-01-2345. Review period: 90 days, start: 03/15.",
        "FMLA paperwork: SSN 265-12-3456, qualifying event: surgery, expected leave: 6 weeks.",
        "Retirement plan enrollment: SSN 376-23-4567, contribution: 6% pre-tax, fund: Target 2045.",
        "Separation agreement signed: SSN 487-34-5678, severance: 8 weeks, effective 03/31.",
        "Tuition reimbursement approval: SSN 598-45-6789, school: State University, \$5,250/year.",
        "Non-compete agreement for SSN 209-56-7890, restricted period: 2 years, area: 50-mile radius.",
        "Relocation assistance package: SSN 320-67-8901, move from Chicago to Seattle, \$8,500.",
        "Stock option grant: SSN 431-78-9012, ISO 10,000 shares at \$12.50, vesting: 4 years.",
    )

    val HR_TEXTS_WITH_PHONE = listOf(
        "New employee emergency contact: Jane Smith, relationship: spouse, phone: (555) 234-5678.",
        "HR hotline for confidential reporting: call +1-800-555-0199 anytime, 24/7.",
        "Benefits enrollment questions: contact Sarah at ext. 4512 or (415) 555-0100.",
        "IT helpdesk for new employee laptop setup: call 212-555-0134 or text 'HELP' to 55555.",
        "Payroll issues? Contact payroll@company.com or call (214) 555-0177 by Thursday EOD.",
        "Schedule interview via HR scheduling line: +1 (312) 555-0122, Mon-Fri 8am-5pm CST.",
        "Employee assistance program (EAP): free confidential counseling, call 1-888-555-0111.",
        "Remote work IT support: call +44 20 7946 0958 for UK employees, M-F 9am-6pm GMT.",
        "Health insurance provider direct line: (800) 555-0133 — ask for the employer group.",
        "Workplace safety incident reporting hotline: (877) 555-0100 — report immediately.",
    )

    val HR_TEXTS_WITH_EMAIL = listOf(
        "All onboarding documents should be submitted to hr.onboarding@company.com by Day 1.",
        "Benefits questions? Email benefits@hrservices.com — response within 2 business days.",
        "Performance review notifications sent to mgmt@internalreview.org on the 15th.",
        "Open enrollment reminders go to all-employees@company.com from benefits@company.com.",
        "Submit expense reports to expenses@finance.company.com with receipts attached.",
        "Learning & Development course catalog available via training@ld.company.com.",
        "Safety training completion certificates: send to safety@compliance.company.com.",
        "IT access requests must CC security@it.company.com for new employee provisioning.",
        "Direct deposit enrollment form: email completed form to payroll@company.com encrypted.",
        "Diversity & inclusion resource group: contact erg.info@company.com for more details.",
    )

    // =========================================================================
    // E-commerce / Retail Context Texts
    // =========================================================================

    val ECOMMERCE_TEXTS_WITH_CARD = listOf(
        "Order #ORD-2024-001234 confirmed. Charged \$89.99 to Visa ending in 1111.",
        "Refund of \$29.95 processed to your MasterCard 5500 0055 5555 5559 within 3-5 days.",
        "Fraudulent charge on Amex 3714 496353 98431 — dispute reference: DISP-2024-7890.",
        "One-click purchase enabled for Visa 4012888888881881 — purchase protection active.",
        "Installment plan activated: \$600 total, 3 payments of \$200 on Discover 6011111111111117.",
        "Declined: card 5105 1051 0510 5100 — ZIP mismatch. Please verify billing address.",
        "Subscription renewed: \$14.99/month on Amex 371449635398431, next billing: 04/01.",
        "Gift card purchase \$100 via Visa 4916679762607382, code emailed to buyer.",
        "Price protection claim approved: Mastercard 5425233430109903, refund: \$45.",
        "Apple Pay transaction: Visa 4539578763621486 (tokenized), merchant: Coffee Shop Downtown.",
        "Contactless payment at self-checkout: Mastercard 5200828282828210, \$52.34.",
        "Loyalty points earned: 890 pts on Discover 6011000990139424, total balance: 12,450.",
        "Return processed: JCB 3530111333300000, refund \$149.99, 5-7 business days.",
        "International purchase blocked: Visa 4024007136512380 — enable international use online.",
        "Pre-order deposit \$100 charged to Mastercard 5555555555554444, ship date: 06/01.",
    )

    val ECOMMERCE_TEXTS_WITH_EMAIL = listOf(
        "Order confirmation sent to customer@email.com — Order ID: ORD-2024-009876.",
        "Shipping notification: package dispatched, tracking email sent to buyer@gmail.com.",
        "Product review request email sent to happy.shopper@yahoo.com, 3 days after delivery.",
        "Abandoned cart reminder sent to potential.buyer@outlook.com — 10% discount offered.",
        "Account verification: confirm email address at newuser@protonmail.com to activate.",
        "Newsletter signup confirmed: subscriber@example.com added to weekly digest list.",
        "Password reset requested for account holder email: j.smith+shop@company.com.",
        "Seller notification: new order from buyer@marketplace.net for 3 units of SKU-44412.",
        "Wholesale inquiry from purchasing@bigboxretail.com — requested catalog and pricing.",
        "Return label emailed to returnrequest@myemail.org — valid for 30 days.",
    )

    // =========================================================================
    // Technology / Development Context Texts
    // =========================================================================

    val TECH_TEXTS_WITH_API_KEY_CONTEXT = listOf(
        "The CI pipeline failed because the AWS key AKIA*** was rotated without updating secrets.",
        "GitHub secret scanning blocked a push containing a Stripe live key in test config.",
        "Developer accidentally committed a .env file with OPENAI_API_KEY to the repository.",
        "Service account credentials in config.json were exposed in a public Docker image.",
        "The legacy microservice still uses a hardcoded API token that hasn't been rotated in 2 years.",
        "Rotate the Anthropic key — it was included in the demo recording shared on YouTube.",
        "Security alert: high-entropy string in PR #456 matching AWS secret key pattern.",
        "Pre-commit hook added to prevent secrets from being committed to any branch.",
        "Vault integration complete — all API keys now retrieved at runtime, none in source code.",
        "Penetration test found API key in browser localStorage — severity: HIGH, fix required.",
    )

    val TECH_TEXTS_WITH_IP = listOf(
        "Server at 203.0.113.42 is returning 502 Bad Gateway — check nginx logs immediately.",
        "Client connected from 198.51.100.15 was rate-limited after 1000 requests in 60 seconds.",
        "Firewall rule added: block all traffic from 192.0.2.0/24 (TEST-NET-1).",
        "Load balancer health check failed for backend 10.0.1.15 — removed from pool.",
        "New deployment at internal address 172.16.20.100 — accessible via VPN only.",
        "DDoS attack originating from 45.33.32.156 and 104.21.45.81 — mitigation active.",
        "SSH login from unfamiliar IP 203.161.45.22 — MFA challenge sent to admin.",
        "Analytics service reporting from 8.8.8.8? Confirm Google DNS is not being counted.",
        "CDN cache hit ratio low for origin 151.101.1.57 — investigate TTL configuration.",
        "IPv6 support enabled: server now reachable at 2001:db8::1 as well as 203.0.113.42.",
    )

    val TECH_TEXTS_NO_PII = listOf(
        "The new microservices architecture uses Kubernetes for container orchestration at scale.",
        "Unit test coverage improved from 72% to 89% after adding parameterized test cases.",
        "The database migration script runs in under 3 seconds on the production dataset.",
        "Dependency scanning found 4 vulnerabilities in third-party libraries — 2 critical.",
        "The GraphQL API now supports batched queries, reducing round-trips by 60%.",
        "Code review guidelines updated: all PRs require at least two approvals before merging.",
        "Deployment to production completed successfully — zero downtime with blue-green switch.",
        "The CI/CD pipeline now runs on self-hosted runners, cutting build time by 40%.",
        "New feature flags framework allows safe A/B testing without deploying new releases.",
        "The monorepo migration is complete — all 47 services now in a single repository.",
    )

    // =========================================================================
    // Education Context Texts
    // =========================================================================

    val EDUCATION_TEXTS_WITH_SSN = listOf(
        "FAFSA application submitted for SSN 201-34-5678; EFC: \$0; Pell Grant eligible.",
        "Scholarship award letter: SSN 312-45-6789 awarded \$15,000 Dean's Merit Scholarship.",
        "Transcript request for SSN 423-56-7890 — student number 20210034, degree: BS Computer Science.",
        "Teacher certification renewal: SSN 534-67-8901, license expires 06/30/2025.",
        "Student loan exit counseling: SSN 145-78-9012, total loans: \$32,450, first payment: 10/01.",
        "Background check for SSN 256-89-0123 — field placement at elementary school cleared.",
        "529 plan contribution: SSN 367-90-1234, beneficiary: child, contribution: \$2,000.",
        "Alumni giving record: SSN 478-01-2345, class of 2012, cumulative donations: \$4,500.",
        "Athletic scholarship for SSN 589-12-3456 — sport: soccer, effective: Fall 2024.",
        "Graduation audit: SSN 190-23-4567 — all requirements met, expected conferral: May 2024.",
    )

    val EDUCATION_TEXTS_WITH_EMAIL = listOf(
        "Acceptance letter emailed to student.applicant@gmail.com — enrollment deadline: May 1.",
        "Course registration confirmation to john.doe.2025@university.edu — CRN: 12345.",
        "Financial aid award letter emailed to student@myschool.org — review and accept online.",
        "Professor office hours reminder: contact dr.smith@college.edu to schedule appointment.",
        "Tutoring center: book session at tutoring@learningcenter.edu — 2-day advance notice.",
        "Study abroad interest form: submit to abroad@international.university.edu by March 31.",
        "Disability services: contact access@disabilities.edu for accommodations planning.",
        "Career center job posting: apply via career.services@university.edu with cover letter.",
        "Library fine notice emailed to student@uni.edu — \$12.50 due, books overdue 25 days.",
        "Dormitory maintenance request confirmation: sent to housing@residence.edu, ticket #4892.",
    )

    // =========================================================================
    // Travel Context Texts
    // =========================================================================

    val TRAVEL_TEXTS_WITH_CARD = listOf(
        "Flight booking confirmed, PNR: AXYZ12 — charged \$487 to Visa 4111111111111111.",
        "Hotel reservation: Marriott Times Square, check-in 04/10, card on file Amex 378282246310005.",
        "Car rental pre-auth: \$450 hold placed on Mastercard 5500005555555559 at Hertz.",
        "Travel insurance purchased: \$89 charged to Discover 6011111111111117 for 2-week trip.",
        "Airport lounge day pass: \$50 on Amex 371449635398431 — Centurion Club access.",
        "Priority boarding upgrade: \$125 on Visa 4916338506082832 for seat 3A, flight UA 456.",
        "International data plan: \$35 charged to Mastercard 5425233430109903 for 30-day pass.",
        "Cruise deposit: \$500 on Visa 4539578763621486, balance due 60 days before sailing.",
        "Currency exchange at airport: \$500 USD to EUR 462 — fee: \$12, card: 4929415432944605.",
        "Travel emergency medical claim: \$3,200 covered, card reimbursement to 5200828282828210.",
    )

    val TRAVEL_TEXTS_WITH_PHONE = listOf(
        "Flight change notification sent via SMS to +1 (555) 234-5678 and email.",
        "Hotel concierge direct line: +44 20 7946 0958 — available 24/7 for guest requests.",
        "Airport shuttle pickup: text 'READY' to (800) 555-0145 when at arrivals terminal.",
        "Travel emergency hotline: +1-877-555-0100 — collect calls accepted from abroad.",
        "Rental car roadside assistance: call +1 (800) 555-0166 from anywhere in North America.",
        "Tour group meeting point confirmation: call guide at +49 170 1234567 on arrival.",
        "Airline customer service: +1 (800) 433-7300 (AA) or +1 (800) 221-1212 (Delta).",
        "Visa inquiry for Japan travel: contact +81 3 3224 5000 (Embassy in Tokyo).",
        "Travel clinic appointment: call (415) 555-0100 for vaccination requirements.",
        "Lost luggage hotline: +1 (800) 555-0177 — have PNR and bag tag number ready.",
    )

    // =========================================================================
    // Safe Non-PII Texts (various contexts)
    // =========================================================================

    val SAFE_TEXTS = listOf(
        "The mitochondria is the powerhouse of the cell.",
        "Photosynthesis converts sunlight, water, and CO₂ into glucose and oxygen.",
        "The speed of light in a vacuum is approximately 299,792 kilometers per second.",
        "Shakespeare wrote 37 plays and 154 sonnets during his lifetime.",
        "The Great Wall of China is approximately 21,196 kilometers long.",
        "Water boils at 100°C (212°F) at sea level under standard atmospheric pressure.",
        "The Amazon rainforest produces about 20% of the world's oxygen supply.",
        "The first iPhone was introduced by Steve Jobs on January 9, 2007.",
        "Mount Everest is the highest point on Earth at approximately 8,849 meters.",
        "The Fibonacci sequence begins: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34...",
        "Beethoven was deaf when he composed his Ninth Symphony.",
        "The periodic table currently has 118 confirmed elements.",
        "The human body contains approximately 37.2 trillion cells.",
        "The Mona Lisa was painted by Leonardo da Vinci between 1503 and 1519.",
        "Pi (π) is approximately 3.14159265358979323846...",
        "The Wright Brothers achieved the first powered flight on December 17, 1903.",
        "The universe is estimated to be approximately 13.8 billion years old.",
        "DNA (deoxyribonucleic acid) carries the genetic instructions for all known life.",
        "The stock market index S&P 500 tracks 500 large US publicly traded companies.",
        "A standard chess game begins with 32 pieces on a 64-square board.",
        "The Berlin Wall fell on November 9, 1989, reunifying East and West Germany.",
        "Gravity on the Moon is about 1/6th of Earth's surface gravity.",
        "The Python programming language was created by Guido van Rossum in 1991.",
        "The Eiffel Tower sways up to 7 centimeters in strong winds.",
        "A byte consists of 8 bits; a kilobyte is 1,024 bytes.",
        "The human brain contains approximately 86 billion neurons.",
        "The Colosseum in Rome could hold up to 80,000 spectators.",
        "Albert Einstein developed the theory of general relativity in 1915.",
        "The tallest building in the world is the Burj Khalifa at 828 meters.",
        "Honey bees communicate by performing a waggle dance to indicate food sources.",
    )
}
