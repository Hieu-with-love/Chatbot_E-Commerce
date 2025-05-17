# Chatbot E-Commerce

An Android e-commerce application with integrated Gemini AI chatbot.

## Setup Instructions

### API Keys

To run this application, you'll need a Google Gemini AI API key:

1. Get your API key from [Google AI Studio](https://ai.google.dev/)
2. Create a file named `local.properties` in the project root (if it doesn't exist already)
3. Add your API key to the file:
   ```properties
   # Other properties might be here
   
   # Gemini AI Configuration
   API_KEY=your_api_key_here
   MODEL_NAME=gemini-2.0-flash
   ```

**Note**: The `local.properties` file is automatically excluded from Git, so your API key will remain private.

### Building the Project

- Open the project in Android Studio
- Sync the project with Gradle files
- Run the application on an emulator or physical device

## Security Notes

- Never commit API keys or sensitive credentials to Git
- The application is configured to read API keys from `local.properties`, which is ignored by Git
- If you need to share the project with others, direct them to obtain their own API keys

## Features

- AI-powered chatbot using Google Gemini
- [Other features of your application]
