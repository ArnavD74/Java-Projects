# Photo Management System

## Project Overview

This is a Java-based application that allows users to manage and organize photo albums. The app includes functionalities such as creating, opening, deleting, renaming albums, adding/removing/displaying photos, tagging photos with location or person tags, and searching for photos by tag. Additionally, the app includes features for managing user roles with stock and admin users.

---

## Features

### 1. **Home Screen**

-   **Display albums in list**
    -   The home screen lists all available photo albums.
-   **Persistence across multiple launches of the app**
    -   Albums are saved and persist across app launches.

### 2. **Open, Create, Delete, Rename Albums**

-   **Open**
    -   Users can open an album to view its photos.
-   **Create**
    -   Users can create new albums.
-   **Delete**
    -   Users can delete an album.
-   **Rename**
    -   Users can rename an existing album.

### 3. **Add, Remove, Display Photo**

-   **Add**
    -   Users can add photos to an album.
-   **Remove**
    -   Users can remove photos from an album.
-   **Display screen for single photo**
    -   Users can view a single photo in full-screen mode.
-   **Slideshow in display screen**
    -   A slideshow feature is available to view multiple photos in sequence.

### 4. **Adding/Deleting Tags to Photo**

-   **Adding person tag**
    -   Users can add person tags to photos.
-   **Adding location tag**
    -   Users can add location tags to photos.
-   **Deleting tag**
    -   Users can delete tags from photos.
-   **Displaying tag with photo**
    -   Tags are displayed along with the photos.

### 5. **Move Photo Between Albums**

-   Users can move photos between different albums.

### 6. **Search Photos by Tag**

-   **By person only (no prefix completion)**
    -   Photos can be searched by a specific person tag.
-   **By location only (no prefix completion)**
    -   Photos can be searched by a specific location tag.
-   **Completion of prefix (either person or location only, not both)**
    -   The search supports tag prefix completion for both person and location.
-   **Conjunction: person AND location (no prefix completion)**
    -   Users can search for photos that have both person and location tags.
-   **Disjunction: person OR location (no prefix completion)**
    -   Users can search for photos with either person or location tags.

### 7. **User Management**

-   **Stock user login**
    -   A stock user can log in using the username `stock`.
-   **Admin login**
    -   An admin can log in using the username `admin`.
-   **Passwords are nonfunctional**
    -   Passwords are not currently used or functional in this version of the app.

### 8. **Stock Photos**

-   **Stock photos location**
    -   Stock photos are located in `/src/stock/` directory.
-   **Photo loading**
    -   The image file paths are loaded from the disk the first time the project is opened.

---
