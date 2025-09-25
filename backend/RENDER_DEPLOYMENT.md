# Deploying SummerVeld Hound Resort API to Render

This guide will help you deploy your backend API to Render, a modern cloud platform for hosting web services.

## Prerequisites

1. A Render account (free tier available)
2. Your code pushed to a Git repository (GitHub, GitLab, or Bitbucket)
3. Firebase project (for database and authentication)
4. Supabase account (for image storage)

## Step 1: Prepare Your Repository

1. **Ensure your code is committed and pushed to your Git repository**
2. **Create a `.env.production` file** (optional, for local reference):
   ```bash
   NODE_ENV=production
   PORT=5000
   FRONTEND_URL=https://your-frontend-app.onrender.com
   
   # Firebase configuration (Primary Database)
   FIREBASE_PROJECT_ID=your-firebase-project-id
   FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_HERE\n-----END PRIVATE KEY-----\n"
   FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
   
   # JWT secrets (generate strong, random strings)
   JWT_SECRET=your-super-secret-jwt-key-for-production
   JWT_EXPIRE=7d
   JWT_REFRESH_SECRET=your-super-secret-refresh-key-for-production
   JWT_REFRESH_EXPIRE=30d
   
   # Supabase configuration (for image storage)
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_SERVICE_ROLE_KEY=your-supabase-service-role-key
   SUPABASE_BUCKET_NAME=images
   ```

## Step 2: Deploy to Render

### Option A: Using Render Dashboard (Recommended for beginners)

1. **Go to [Render Dashboard](https://dashboard.render.com/)**
2. **Click "New +" → "Web Service"**
3. **Connect your Git repository**
4. **Configure your service**:
   - **Name**: `summerveld-api`
   - **Runtime**: `Node`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
   - **Root Directory**: `backend` (if your backend is in a subfolder)

### Option B: Using render.yaml (Automated deployment)

If you have the `render.yaml` file in your repository:

1. **Go to Render Dashboard**
2. **Click "New +" → "Blueprint"**
3. **Connect your Git repository**
4. **Render will automatically detect and use the `render.yaml` configuration**

## Step 3: Configure Environment Variables

In your Render service dashboard, go to the "Environment" tab and add these variables:

### Required Variables:
```
NODE_ENV=production
FRONTEND_URL=https://your-frontend-app.onrender.com
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_HERE\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
JWT_SECRET=your-super-secret-jwt-key-for-production
JWT_EXPIRE=7d
JWT_REFRESH_SECRET=your-super-secret-refresh-key-for-production
JWT_REFRESH_EXPIRE=30d
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_ROLE_KEY=your-supabase-service-role-key
SUPABASE_BUCKET_NAME=images
```

## Step 4: Set Up Firebase (Production Database)

1. **Go to [Firebase Console](https://console.firebase.google.com/)**
2. **Select your existing project** or create a new one
3. **Enable Firestore Database**:
   - Go to Firestore Database
   - Click "Create database"
   - Choose "Start in test mode" (you can secure it later)
4. **Get your Firebase credentials**:
   - Go to Project Settings → Service Accounts
   - Click "Generate new private key"
   - Download the JSON file
5. **Update environment variables in Render** with your Firebase credentials

## Step 5: Set Up Supabase (for image storage)

1. **Create a Supabase project** at [supabase.com](https://supabase.com)
2. **Go to Settings → API** to get your URL and service role key
3. **Create a storage bucket** named `images`
4. **Update the Supabase environment variables** in Render

## Step 6: Deploy and Test

1. **Click "Deploy"** in Render
2. **Wait for deployment to complete** (usually 2-5 minutes)
3. **Test your API endpoints**:
   - Health check: `https://your-app.onrender.com/health`
   - API routes: `https://your-app.onrender.com/api/auth/login`

## Step 7: Update Your Mobile App

Update your Android app to use the new Render API URL:

1. **Find your network configuration files** in the Android app
2. **Update the base URL** to point to your Render deployment
3. **Test the connection** from your mobile app

## Troubleshooting

### Common Issues:

1. **Build fails**: Check that all dependencies are in `package.json`
2. **App crashes on startup**: Check environment variables are set correctly
3. **Firebase connection fails**: Verify Firebase credentials and project ID
4. **CORS errors**: Update the `FRONTEND_URL` environment variable

### Checking Logs:

1. **Go to your Render service dashboard**
2. **Click on "Logs" tab** to see real-time logs
3. **Check for error messages** and fix accordingly

## Security Considerations

1. **Use strong, unique secrets** for JWT tokens
2. **Secure your Firestore database** with proper security rules
3. **Use HTTPS** (automatically provided by Render)
4. **Keep environment variables secure** and never commit them to Git
5. **Set up Firebase security rules** for your Firestore collections

## Monitoring and Maintenance

1. **Set up monitoring** using Render's built-in metrics
2. **Set up alerts** for service downtime
3. **Regular backups** of your Firestore data
4. **Update dependencies** regularly for security patches

## Cost Optimization

- **Free tier**: 750 hours/month (sufficient for development)
- **Paid plans**: Start at $7/month for always-on services
- **Database**: Firebase Firestore free tier provides 1GB storage and 50K reads/writes per day

## Next Steps

1. **Set up custom domain** (optional)
2. **Configure SSL certificates** (automatically handled by Render)
3. **Set up CI/CD** for automatic deployments
4. **Monitor performance** and optimize as needed

---

Your API should now be live at `https://your-app-name.onrender.com`! 🚀
