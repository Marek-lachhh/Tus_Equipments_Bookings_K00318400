# TUS Equipment Booking (K00318400)

Android app for browsing TUS equipment and adding new equipment to the catalogue.

Kotlin, Jetpack Compose, Material 3, a ViewModel and Room. Screens are switched with a simple screen variable (no Navigation library). Same Gradle setup as the class labs (AGP 9.4.1, Gradle 9.6.0, SDK 37, min SDK 24).

## Run

1. Open the `Tus_Equipments_Bookings_K00318400` folder in Android Studio.
2. Sync Gradle and run `app` on an emulator or phone (Android 7.0 or newer).

## Current stage

- Screens: Home, Equipment, Equipment Details, Add Equipment, Profile.
- Category and Equipment models with linked sample data (Equipment.categoryId → Category.id).
- Reusable EquipmentCard component.
- Room database in `data/local`: EquipmentDatabase, EquipmentDao and EquipmentRepository. Equipment.categoryId is a foreign key to Category.id and is indexed.
- Sample categories and equipment are added once on first launch. Equipment added in the app is saved and stays after the app is closed.
- Add Equipment form with name, category and description. Name and description cannot be blank.

## Not added yet

- Bookings.
- Firebase login and logout.
