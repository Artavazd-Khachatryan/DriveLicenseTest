# Database Setup for Builds

This document explains how to set up a pre-populated database that will be included in your app builds.

## Overview

The app now supports including a pre-populated database with all question data in each build. This eliminates the need to initialize the database from scratch every time the app starts.

## Current Setup

1. **Database Initialization**: The app currently initializes the database on first run
2. **Export Capability**: Added utilities to export the populated database
3. **Pre-populated Loading**: Added support for loading pre-populated databases

## How to Create a Pre-populated Database

### Step 1: Populate the Database
1. Run the app and ensure all questions are loaded
2. The app will automatically populate the database on first run

### Step 2: Export the Database
1. Use the `DatabaseLoader.exportDatabase()` method to export the database
2. Copy the exported database file to `src/commonMain/resources/prepopulated_database.db`

### Step 3: Include in Build
The database file will automatically be included in the app's assets and loaded on first run.

## Implementation Details

### Files Created/Modified:
- `DatabasePreloader.kt` - Handles database export/import
- `DatabaseLoader.kt` - Platform-specific database loading
- `DatabaseInitializer.kt` - Updated to try loading pre-populated database first
- `DatabaseExportUtil.kt` - Utility for exporting database
- `build.gradle.kts` - Updated to include database in assets

### Database Loading Process:
1. App starts
2. `DatabaseInitializer` tries to load pre-populated database from assets
3. If found, database is copied to app's database directory
4. If not found, falls back to normal initialization process

## Benefits

1. **Faster App Startup**: No need to initialize database on first run
2. **Consistent Data**: All builds have the same question data
3. **Offline Support**: Database is included in the app bundle
4. **Reduced Network Usage**: No need to download question data

## Development Workflow

1. **Development**: Use normal database initialization for testing
2. **Pre-release**: Export populated database and include in build
3. **Release**: App includes pre-populated database for instant startup

## Next Steps

To complete the setup:

1. Run the app and ensure all questions are loaded
2. Use the export functionality to create the pre-populated database
3. Include the database file in your builds
4. Test that the app loads the pre-populated database correctly

The app will now start much faster since it doesn't need to initialize the database from scratch! 