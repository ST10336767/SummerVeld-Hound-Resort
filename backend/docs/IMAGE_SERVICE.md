# Image Service Documentation

This document describes the Supabase-based image service implementation for the SummerVeld Hound Resort API.

## Overview

The image service provides comprehensive image upload, storage, and retrieval functionality using Supabase Storage. It includes image processing, optimization, and secure access controls.

## Features

- ✅ **Image Upload**: Single and multiple image uploads
- ✅ **Image Processing**: Automatic resizing and optimization with Sharp
- ✅ **Secure Storage**: Supabase Storage with public and private access
- ✅ **File Validation**: Type and size validation
- ✅ **Signed URLs**: Temporary access for private images
- ✅ **Organized Storage**: Folder-based organization
- ✅ **Profile Images**: Specialized endpoints for user and pet profiles

## Setup

### 1. Supabase Configuration

1. Create a Supabase project at [supabase.com](https://supabase.com)
2. Create a storage bucket named `images` (or configure your preferred name)
3. Set up Row Level Security (RLS) policies for your bucket
4. Get your project URL and service role key

### 2. Environment Variables

Add these variables to your `.env` file:

```env
# Supabase Configuration
SUPABASE_URL=your-supabase-project-url
SUPABASE_SERVICE_ROLE_KEY=your-supabase-service-role-key
SUPABASE_BUCKET_NAME=images
```

### 3. Install Dependencies

```bash
npm install @supabase/supabase-js multer sharp uuid
```

## API Endpoints

### Upload Single Image
```http
POST /api/images/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- image: File (required)
- folder: String (optional)
- width: Number (optional, 50-4000)
- height: Number (optional, 50-4000)
- quality: Number (optional, 1-100)
- format: String (optional, jpeg|png|webp)
```

**Response:**
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "data": {
    "fileName": "uuid-generated-name.jpg",
    "filePath": "folder/uuid-generated-name.jpg",
    "publicUrl": "https://your-project.supabase.co/storage/v1/object/public/images/folder/uuid-generated-name.jpg",
    "size": 245760,
    "originalName": "original-image.jpg"
  }
}
```

### Upload Multiple Images
```http
POST /api/images/upload-multiple
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- images: File[] (required, max 5 files)
- folder: String (optional)
- width: Number (optional)
- height: Number (optional)
- quality: Number (optional)
- format: String (optional)
```

### Upload Pet Profile Image
```http
POST /api/images/pet-profile
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- image: File (required)
- petId: String (required, MongoDB ObjectId)
```

### Upload User Profile Image
```http
POST /api/images/user-profile
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- image: File (required)
```

### Get Image URL
```http
GET /api/images/url/:filePath
Authorization: Bearer <token>
```

### Create Signed URL
```http
POST /api/images/signed-url
Content-Type: application/json
Authorization: Bearer <token>

Body:
{
  "filePath": "folder/image.jpg",
  "expiresIn": 3600
}
```

### List Images
```http
GET /api/images/list?folder=pets&limit=20&offset=0
Authorization: Bearer <token>
```

### Delete Image
```http
DELETE /api/images/:filePath
Authorization: Bearer <token>
```

## File Organization

Images are organized in folders within the Supabase bucket:

```
images/
├── users/
│   └── {userId}/
│       └── profile/
│           └── profile-image.jpg
├── pets/
│   └── {petId}/
│       ├── profile/
│       │   └── profile-image.jpg
│       └── gallery/
│           ├── image1.jpg
│           └── image2.jpg
└── general/
    ├── service-images/
    └── facility-images/
```

## Image Processing

The service automatically processes uploaded images:

- **Resizing**: Images are resized to fit within specified dimensions
- **Optimization**: Images are compressed for web delivery
- **Format Conversion**: Images can be converted to different formats
- **Quality Control**: Adjustable quality settings

### Default Processing Settings

- **Profile Images**: 300x300px (users), 400x400px (pets)
- **Quality**: 90% for profile images, 85% for general images
- **Format**: JPEG by default
- **Max Size**: 5MB per file

## Security Features

### File Validation
- **Allowed Types**: JPEG, PNG, WebP, GIF
- **Size Limits**: Configurable maximum file size
- **File Count**: Maximum 5 files per multiple upload

### Access Control
- **Authentication**: All endpoints require valid JWT token
- **Authorization**: Users can only access their own images
- **Signed URLs**: Temporary access for private content

### Supabase RLS Policies

Example RLS policies for the images bucket:

```sql
-- Allow authenticated users to upload images
CREATE POLICY "Users can upload images" ON storage.objects
FOR INSERT WITH CHECK (auth.role() = 'authenticated');

-- Allow users to view their own images
CREATE POLICY "Users can view own images" ON storage.objects
FOR SELECT USING (auth.uid()::text = (storage.foldername(name))[1]);

-- Allow users to delete their own images
CREATE POLICY "Users can delete own images" ON storage.objects
FOR DELETE USING (auth.uid()::text = (storage.foldername(name))[1]);
```

## Error Handling

The service provides comprehensive error handling:

### Common Errors

- **400 Bad Request**: Invalid file type, size, or missing parameters
- **401 Unauthorized**: Missing or invalid authentication token
- **403 Forbidden**: Insufficient permissions
- **413 Payload Too Large**: File size exceeds limits
- **500 Internal Server Error**: Server-side processing errors

### Error Response Format

```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message (development only)"
}
```

## Usage Examples

### Frontend Integration

#### Upload Single Image (React)
```javascript
const uploadImage = async (file, folder = '') => {
  const formData = new FormData();
  formData.append('image', file);
  formData.append('folder', folder);
  formData.append('width', '800');
  formData.append('quality', '85');

  const response = await fetch('/api/images/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });

  return response.json();
};
```

#### Display Image
```javascript
const displayImage = (filePath) => {
  const imageUrl = `https://your-project.supabase.co/storage/v1/object/public/images/${filePath}`;
  return <img src={imageUrl} alt="Uploaded image" />;
};
```

### Backend Integration

#### Save Image Reference to Database
```javascript
// After successful upload
const booking = await Booking.findByIdAndUpdate(
  bookingId,
  { 
    'pet.profileImage': result.data.filePath,
    'pet.profileImageUrl': result.data.publicUrl
  },
  { new: true }
);
```

## Performance Considerations

### Optimization Tips

1. **Image Sizing**: Always specify appropriate dimensions
2. **Quality Settings**: Balance quality vs file size
3. **Format Selection**: Use WebP for better compression
4. **Caching**: Leverage Supabase CDN for faster delivery

### Monitoring

- Monitor storage usage in Supabase dashboard
- Track upload success/failure rates
- Monitor response times for image operations

## Troubleshooting

### Common Issues

1. **Upload Fails**: Check file size and type restrictions
2. **Images Not Loading**: Verify Supabase bucket policies
3. **Slow Uploads**: Check network connection and file size
4. **Authentication Errors**: Verify JWT token validity

### Debug Mode

Enable debug logging by setting:
```env
NODE_ENV=development
```

This will provide detailed error messages and stack traces.

## Future Enhancements

- [ ] Image cropping and editing
- [ ] Automatic thumbnail generation
- [ ] Image metadata extraction
- [ ] Batch processing capabilities
- [ ] Image watermarking
- [ ] Advanced compression algorithms
