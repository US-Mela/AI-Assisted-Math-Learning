name: Flutter Unit Test

on:
  push:
    branches:
      - test
  pull_request:
    branches:
      - test

jobs:
  test:
    name: Run Flutter Tests
    runs-on: ubuntu-latest

  steps:
    - name: Checkout repository
      uses: actions/checkout@v4

    - name: Get to folder
      run: cd frontend/mela

    - name: Setup Flutter
      uses: subosito/flutter-action@v2
      with:
        flutter-version: 3.27.4  # You can specify the Flutter version to use

    - name: Install dependencies
      run: flutter pub get

    - name: Run tests
      run: flutter test
