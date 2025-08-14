# COMP2000_fresh_repo_CW2_referral

# Library Manager – COMP2000 Assessment 2

This repository contains my Android Studio project for the public library management application developed as part of COMP2000 Assessment 2.

## Project Overview
The application supports both **staff** and **member** modes:

- **Staff Mode** – Allows library staff to:
  - Manage member details (add, update, delete)
  - Maintain the local book catalogue (using SQLite)
  - Approve or deny book requests (triggers notifications)

- **Member Mode** – Allows library members to:
  - Browse and search the book catalogue
  - Request books
  - Update their personal profile
  - Receive notifications on request status

The book catalogue is stored locally using SQLite, while member data and issued book records are accessed via the supplied RESTful API.

## Login Details for Testing

### Staff Login
Username: staff1
Password: pass123

### Member Login
Username: member1
Password: pass123


These accounts are seeded locally in the `users` table when the app’s database is first created.

## Technologies Used
- **Java** (Android Studio)
- **SQLiteOpenHelper** for local data
- **RecyclerView** for displaying lists
- **Volley** for API calls
- **NotificationCompat** for push notifications

2. Open in Android Studio.
3. Ensure an emulator or connected device is available.
4. Build and run the project.

## Credits
All third-party libraries are included via Gradle:
- Volley (Google)
- AndroidX libraries

Icons and resources:
- Material Icons – © Google 
- All other assets created by me for this project.


