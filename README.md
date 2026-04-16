# 📺 Subwatcher

> **Know what you're paying for.**  
> Subwatcher is an Android app that helps you track, manage, and stay on top of all your subscriptions in one place.

> ⚠️ **This project is currently a work in progress.** Features and the overall design are subject to change as development continues.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Building from Source](#building-from-source)
- [Usage](#usage)
- [Permissions](#permissions)
- [Contributing](#contributing)
- [Roadmap](#roadmap)
- [License](#license)

---

## 🔍 Overview

**Subwatcher** is an Android application built in **Java** using **Android Studio** that lets you keep track of every subscription you're paying for — streaming services, apps, memberships, and more.

It's easy to lose track of recurring charges spread across different platforms and billing cycles. Subwatcher puts them all in one place so you always know what's coming, what it costs, and when it renews.

---

## ✨ Features

> ⚠️ Some features listed below are planned and not yet fully implemented. See the [Roadmap](#roadmap) for current progress.

### 📋 Subscription Tracking
- Add, edit, and delete subscriptions manually
- Set subscription name, cost, billing cycle (monthly / yearly / weekly), and start date
- Categorize subscriptions (Entertainment, Productivity, Fitness, etc.)

### 💸 Cost Overview
- See your total monthly and yearly spending at a glance
- Per-subscription cost breakdown

### 🔔 Renewal Reminders
- Get notified before a subscription renews
- Customizable reminder timing (e.g. 1 day, 3 days, 1 week before)

### 📅 Billing Calendar
- View upcoming renewals on a calendar
- Never be caught off guard by a charge again

### 🗂️ Subscription Management
- Mark subscriptions as active or inactive / cancelled
- Filter and sort by cost, renewal date, or category

### 🎨 UI & Design
- Clean, minimal interface
- Dark mode support

---

## 🛠️ Tech Stack

| Technology | Details |
|---|---|
| **Language** | Java |
| **IDE** | Android Studio |
| **Min SDK** | Android 8.0 (API 26) |
| **Target SDK** | Android 14 (API 34) |
| **Build System** | Gradle |
| **Architecture** | Activity-based (MVC) |

---

## 🚀 Getting Started

### Prerequisites

Before you begin, make sure you have the following installed:

- [Android Studio](https://developer.android.com/studio) (Hedgehog or newer recommended)
- Java Development Kit (JDK 11 or higher)
- Android SDK with API level 26+
- A physical Android device **or** an Android emulator

---

### Building from Source

1. **Clone the repository**

```bash
git clone https://github.com/your-username/Subwatcher.git
cd Subwatcher
```

2. **Open in Android Studio**

   - Launch Android Studio
   - Select **File → Open**
   - Navigate to the cloned `Subwatcher` folder and open it

3. **Sync Gradle**

   Android Studio will prompt you to sync Gradle automatically. Click **Sync Now** if it doesn't start on its own.

4. **Run the app**

   - Connect a physical device via USB (with USB Debugging enabled) or start an emulator
   - Click the **▶ Run** button or press `Shift + F10`

---

## 📖 Usage

1. **Open Subwatcher** — the home screen shows an overview of your active subscriptions and total monthly cost.
2. **Add a Subscription** — tap the **+** button and fill in the name, cost, billing cycle, and renewal date.
3. **Browse Your Subs** — scroll through your subscription list, filter by category, or sort by cost or renewal date.
4. **Check Upcoming Renewals** — head to the calendar view to see what's renewing and when.
5. **Set Reminders** — configure notification reminders so you're always warned before a charge hits.
6. **Mark as Cancelled** — no longer subscribed? Mark it inactive to keep a record without cluttering your active list.

---

## 🔐 Permissions

Subwatcher requests only the permissions it needs:

| Permission | Purpose |
|---|---|
| `POST_NOTIFICATIONS` | Send renewal reminder notifications |
| `RECEIVE_BOOT_COMPLETED` | Reschedule reminders after device reboot |
| `SCHEDULE_EXACT_ALARM` | Trigger reminders at precise renewal times |

> Subwatcher does **not** connect to the internet, access your bank or payment info, or share any data. Everything stays local on your device.

---

## 🤝 Contributing

Contributions are welcome! Since the project is still in early development, feel free to open issues for bugs, suggestions, or feature ideas.

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add: your feature description"`
4. Push to your fork: `git push origin feature/your-feature-name`
5. Open a **Pull Request** with a clear description of what you changed and why

---

## 🗺️ Roadmap

- [ ] Add / edit / delete subscriptions
- [ ] Monthly and yearly cost totals
- [ ] Billing cycle support (weekly / monthly / yearly)
- [ ] Subscription categories
- [ ] Renewal reminder notifications
- [ ] Billing calendar view
- [ ] Active / inactive / cancelled status tracking
- [ ] Filter and sort options
- [ ] Dark mode support
- [ ] Data export (CSV or JSON)
- [ ] Home screen widget for upcoming renewals
- [ ] Currency selection support

---

## 📄 License

```
MIT License

Copyright (c) 2026 Brayden Kovarsky-Steingold

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

Made with ☕ and Java by **Brayden Kovarsky-Steingold**

</div>
