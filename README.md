# 🛡️ Browser Security Monitor

> **A real-time enterprise browser security extension that detects phishing and malicious websites using a rule-based risk scoring engine with Spring Boot backend.**

[![Java](https://img.shields.io/badge/Java-21-007396?style=flat&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-6DB33F?style=flat&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Chrome Extension](https://img.shields.io/badge/Chrome%20Extension-4285F4?style=flat&logo=google-chrome&logoColor=white)](https://developer.chrome.com/docs/extensions/)

---

## 📌 Table of Contents

- [About The Project](#-about-the-project)
- [Key Features](#-key-features)
- [Technologies Used](#-technologies-used)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [How to Run](#-how-to-run)
- [Screenshots](#-screenshots)
- [Future Improvements](#-future-improvements)
- [Contact](#-contact)

---

## 📖 About The Project

**Browser Security Monitor** is an enterprise-grade security solution that protects users from phishing, malware, and malicious websites in real-time.

### 🎯 The Problem It Solves

| Challenge | Impact |
|---|---|
| Employees unknowingly visit phishing websites | Credential theft, data breaches |
| Existing security tools send data to cloud | Privacy concerns, compliance violations |
| No visibility into employee browsing risks | Delayed threat response |
| Expensive enterprise security solutions | Budget constraints for startups/SMEs |
| No tracking of installed browser extensions | Shadow IT security risks |

### ✅ Our Solution

A lightweight Chrome extension + Spring Boot backend that:

1. **Monitors** every URL visited in real-time
2. **Scores** risk using a configurable 6-rule detection engine
3. **Alerts** instantly with red/green badges and system notifications
4. **Tracks** sensitive data movement (copy/paste, file uploads)
5. **Inventories** all installed browser extensions with permissions
6. **Reports** via an interactive executive dashboard with Chart.js visualizations
7. **Exports** CSV reports for compliance and audits

### 🔒 Privacy-First Architecture

- **Everything runs locally** – No external API calls
- **Zero cost** – No cloud subscriptions or usage fees
- **Sub-500ms response time** – Real-time protection without slowdown
- **Complete data ownership** – You control your security data

---

## 🚀 Key Features

### 🔴 Real-Time URL Threat Detection

| Feature | Description | Example |
|---|---|---|
| **6 Detection Rules** | HTTPS check, suspicious keywords, IP addresses, URL length, dot count, URL shorteners | `http://login-test.com` → Score 50 |
| **Risk Scoring** | Dynamic scoring (0-100) with configurable threshold | ≥40 = Dangerous |
| **Instant Alerts** | Red badge (⚠️), system notifications, blocked warnings | Popup appears automatically |
| **Rule Engine** | Extensible rules with detailed reasons for each alert | "No HTTPS, Contains 'login'" |

### 🎨 Visual Indicators

| Indicator | Meaning | Color Code |
|---|---|---|
| 🔴 **Red Badge (!)** | Dangerous/Malicious site detected | #FF0000 |
| 🟢 **Green Badge (✓)** | Safe/Verified site | #4CAF50 |
| 📊 **Risk Score** | 0-100 percentage in popup/dashboard | Dynamic |

### 📊 Enterprise Dashboard

- **Weekly Threat Activity Chart** – Line graph showing threat trends
- **Top 10 Most Dangerous Domains** – List for IT admin review
- **CSV Export** – One-click compliance reporting
- **Live Stats** – Total alerts, average risk score, monitoring status
- **Responsive Design** – Works on any screen size

### 🛡️ Data Loss Prevention (DLP)

| Feature | Description |
|---|---|
| **Copy Monitoring** | Tracks when users copy sensitive content from websites |
| **File Upload Tracking** | Monitors file uploads to external websites |
| **Event Logging** | All DLP events stored in SQLite for forensic analysis |
| **Content Preview** | Stores first 100 characters of copied text for context |

### 📦 Browser Extension Inventory

- **Auto-scan at startup** – Detects all installed extensions
- **Hourly re-scans** – Tracks new extensions
- **Permission tracking** – Captures permissions for risk assessment
- **Internal audit** – Identify high-risk extensions in the organization

### 💬 User Security Training

- **Contextual Alerts** – Educational messages in system notifications
- **Feedback Mechanism** – "Was this alert helpful?" button in popup
- **Continuous Learning** – Feedback stored for future refinement
- **Security Awareness** – Users learn to identify phishing attempts

---

## 🛠️ Technologies Used

### Backend

| Technology | Version | Purpose |
|---|---|---|
| **Java** | 21 | Core programming language |
| **Spring Boot** | 3.4.x | REST API framework |
| **SQLite** | 3.44+ | Embedded database (zero config) |
| **Maven** | 3.9+ | Dependency management |
| **Gson** | 2.10+ | JSON serialization/deserialization |

### Frontend (Chrome Extension)

| Technology | Purpose |
|---|---|
| **Chrome Extension API** | Browser extension foundation |
| **JavaScript (ES6)** | Extension logic and event handling |
| **HTML5/CSS3** | Popup UI with animations |
| **Fetch API** | REST API communication |

### Dashboard

| Technology | Purpose |
|---|---|
| **Chart.js** | Interactive data visualizations |
| **Vanilla JS** | Dashboard interactivity |
| **Responsive CSS** | Professional UI design |
| **REST API** | Fetches real-time data from backend |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Chrome Browser                          │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         Chrome Extension (Content Script)                │  │
│  │  • Monitors copy events                                 │  │
│  │  • Tracks file uploads                                  │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │      Chrome Extension (Background Service Worker)        │  │
│  │  • Listens to tab updates                              │  │
│  │  • Scans installed extensions                          │  │
│  │  • Sends URLs to backend                               │  │
│  │  • Updates badge and notifications                     │  │
│  └──────────────────────┬───────────────────────────────────┘  │
└─────────────────────────┼─────────────────────────────────────┘
                          │ HTTP/REST (localhost:8080)
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Backend                          │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST Controllers                            │  │
│  │  • /check (POST) - URL analysis                        │  │
│  │  • /alerts (GET) - Recent alerts                       │  │
│  │  • /stats (GET) - Dashboard stats                      │  │
│  │  • /api/dashboard/* - Charts & export                  │  │
│  │  • /dlp-event (POST) - DLP logging                    │  │
│  │  • /extension-inventory (POST) - Extension tracking    │  │
│  │  • /training-feedback (POST) - User feedback           │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │          Rule Engine Service                            │  │
│  │  • 6 detection rules                                   │  │
│  │  • Dynamic risk scoring (0-100)                       │  │
│  │  • Configurable threshold (≥40 = dangerous)            │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │          Database Service (SQLite)                      │  │
│  │  • alerts (URL, timestamp, reason, risk_score)         │  │
│  │  • dlp_events (URL, event_type, content_preview)      │  │
│  │  • extension_inventory (name, version, permissions)    │  │
│  │  • training_feedback (helpful, timestamp)             │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
browser-security-monitor/
│
├── .gitignore                              # Git ignore file
├── README.md                               # Project documentation
├── pom.xml                                 # Maven dependencies
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cybersec/
│   │   │           └── browsermonitor/
│   │   │               ├── Maven21Application.java         # Main Spring Boot class
│   │   │               ├── config/
│   │   │               │   └── CorsConfig.java             # CORS configuration
│   │   │               ├── controller/
│   │   │               │   ├── AlertController.java        # /check, /alerts, /stats
│   │   │               │   ├── DashboardController.java    # /api/dashboard/*
│   │   │               │   ├── DlpController.java          # /dlp-event
│   │   │               │   └── InventoryController.java    # /extension-inventory
│   │   │               ├── model/
│   │   │               │   └── Alert.java                  # Alert data model
│   │   │               └── service/
│   │   │                   ├── DatabaseService.java        # SQLite operations
│   │   │                   ├── DashboardService.java       # Dashboard business logic
│   │   │                   └── RuleEngineService.java      # URL detection rules
│   │   └── resources/
│   │       ├── application.properties                      # Spring Boot config
│   │       └── static/
│   │           └── dashboard.html                          # Interactive dashboard UI
│   └── test/
│       └── java/
│
└── chrome-extension/                      # Chrome Extension folder
    ├── manifest.json                      # Extension configuration
    ├── background.js                      # Background service worker
    ├── popup.html                         # Extension popup UI
    ├── popup.js                           # Popup logic
    └── content.js                         # DLP monitoring script
```

---

## 🏃 How to Run

### ✅ Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 21+ |
| Maven | 3.6+ |
| Chrome Browser | Latest |
| Chrome Extension | Developer Mode |

### 🔧 Step 1: Clone & Build

```bash
# Clone the repository
git clone https://github.com/AkashDcse/browser-security-monitor.git
cd browser-security-monitor

# Build the project
mvn clean install

# Run the Spring Boot backend
mvn spring-boot:run
```

**Expected Output:**
```
✅ Database ready
🚀 Browser Monitor Started on http://localhost:8080
```

### 🔌 Step 2: Load Chrome Extension

1. Open Chrome and go to: `chrome://extensions/`
2. Toggle **Developer mode** (top-right corner)
3. Click **Load unpacked**
4. Select the `chrome-extension/` folder from this project
5. Extension icon appears in toolbar

### 🧪 Step 3: Test the System

| Test URL | Expected Result |
|---|---|
| `https://google.com` | Green ✅ badge, Safe |
| `http://login-test.com` | Red ❗ badge, Notification pops up |
| `http://192.168.1.1` | Red ❗ badge + IP address detection |
| `https://bit.ly/abcd` | Red ❗ badge + URL shortener detection |

### 📊 Step 4: View Dashboard

Open your browser and go to: `http://localhost:8080/dashboard.html`

---

## 🖼️ Screenshots

### 🔴 Red Badge Alert (Dangerous Site)
```
┌────────────────────────────┐
│  🔴 [!]  Security Monitor   │
│  ⚠️ DANGEROUS SITE DETECTED │
│  Risk Score: 70/100        │
│  Blocked: login-test.com   │
└────────────────────────────┘
```

### 🟢 Green Badge (Safe Site)
```
┌────────────────────────────┐
│  🟢 [✓]  Security Monitor   │
│  ✅ SAFE SITE              │
│  No threats detected       │
└────────────────────────────┘
```

### 📊 Dashboard Overview
```
┌──────────────────────────────────────────────────────┐
│  🛡️ Security Monitor Dashboard                      │
│  ┌──────────────────────────────────────────────────┐ │
│  │  Weekly Threat Activity                         │ │
│  │  📈 [Chart.js Line Graph]                       │ │
│  │  Mon: 2  Tue: 5  Wed: 3  Thu: 8  Fri: 6       │ │
│  └──────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────┐ │
│  │  Top Dangerous Domains                          │ │
│  │  1. login-test.com (12 alerts)                 │ │
│  │  2. verify-account.net (8 alerts)              │ │
│  │  3. secure-update.xyz (5 alerts)               │ │
│  └──────────────────────────────────────────────────┘ │
│  [📥 Export CSV]                                     │
└──────────────────────────────────────────────────────┘
```

### 📋 Popup with Alerts
```
┌────────────────────────────────┐
│  🛡️ Security Monitor           │
│  🔍 Monitoring your browsing... │
│  ────────────────────────────── │
│  📋 Recent Alerts:              │
│  🔗 http://login-test.com       │
│  📋 No HTTPS, Contains 'login'  │
│  📊 Risk Score: 70/100          │
│  ────────────────────────────── │
│  [⟳ Refresh Security Status]    │
│  [👍 Was this alert helpful?]   │
└────────────────────────────────┘
```

---

## 🔮 Future Improvements

| Feature | Status | Description |
|---|---|---|
| **AI Integration** | ⚠️ Planned | Local LLM via Ollama for zero-day phishing detection |
| **Real-time WebSocket** | 🔜 Planned | Push notifications without page refresh |
| **User Whitelist/Blacklist** | 🔜 Planned | Allow users to mark safe/dangerous domains |
| **Docker Containerization** | 🔜 Planned | One-command deployment |
| **Multi-user Dashboard** | 🔜 Planned | Admin panel for IT teams |
| **Incident Response Automation** | 🔜 Planned | Auto-block and quarantine malicious sites |
| **CI/CD Pipeline** | 🔜 Planned | GitHub Actions for automated builds |
| **Google Safe Browsing API** | 🔜 Planned | Industry-standard threat intelligence |

---

## 📧 Contact

Akash D  
📧 akashdat2004@gmail.com 
🔗 [LinkedIn]https://www.linkedin.com/in/akash-d-bb00772b8/
🐙 [GitHub]https://github.com/AkashDcse 

---

## ⭐ Show Your Support

If this project helped you, please ⭐ star this repository!

---

**Built with ❤️ by Akash D**

---

## 📌 Interview Talking Points

When a recruiter asks about this project, say:

> *"I built an enterprise-grade browser security solution from scratch. It includes real-time URL threat detection using a 6-rule engine with risk scoring, an executive dashboard with Chart.js visualizations, CSV export for compliance, DLP monitoring for copy/paste and file uploads, and extension inventory tracking. What makes it unique is that everything runs locally—no cloud APIs, no data leaving the user's machine—making it privacy-first and zero-cost. The project is built with Spring Boot, Java 21, and Chrome Extension API."*

---

### 📝 Keywords for Resume

Add these keywords to your resume:
- Spring Boot, Java 21, SQLite
- Chrome Extension API, JavaScript
- Real-time Threat Detection
- Risk Scoring Engine
- REST API Development
- Data Loss Prevention (DLP)
- Executive Dashboard, Chart.js
- Enterprise Security, Phishing Detection
- Privacy-First Architecture
- Maven, RESTful Web Services
- Browser Extension Development
- SQLite Database, JSON, Gson

---

**Built with ❤️ by [Your Name]**
