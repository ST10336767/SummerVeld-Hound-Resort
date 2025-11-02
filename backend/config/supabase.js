const { createClient } = require('@supabase/supabase-js')

// Supabase configuration
const supabaseUrl = process.env.SUPABASE_URL
const supabaseServiceKey = process.env.SUPABASE_SERVICE_ROLE_KEY

if (!supabaseUrl || !supabaseServiceKey) {
  console.error('❌ Missing Supabase configuration. Please check your environment variables.')
  console.error('Required: SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY')
}

// Create Supabase client with service role key for server-side operations
const supabase = createClient(supabaseUrl, supabaseServiceKey, {
  auth: {
    autoRefreshToken: false,
    persistSession: false
  }
})

// Test connection to storage bucket
const testConnection = async () => {
  try {
    const bucketName = process.env.SUPABASE_BUCKET_NAME || 'dog_images'

    // Test storage bucket connection by listing buckets
    const { data: buckets, error: bucketsError } = await supabase.storage.listBuckets()

    if (bucketsError) {
      console.error('❌ Supabase storage connection test failed:', bucketsError.message)
      return false
    }

    // Check if our target bucket exists
    const targetBucket = buckets.find(bucket => bucket.name === bucketName)

    if (!targetBucket) {
      console.warn(`⚠️  Storage bucket '${bucketName}' not found. Available buckets:`, buckets.map(b => b.name))
      console.log('💡 You may need to create the bucket in your Supabase dashboard')
      return false
    }

    // Test bucket access by listing files (this will work even if empty)
    const { error: filesError } = await supabase.storage
      .from(bucketName)
      .list('', { limit: 1 })

    if (filesError) {
      console.error('❌ Supabase bucket access test failed:', filesError.message)
      return false
    }

    console.log('✅ Supabase storage connected successfully')
    console.log(`📦 Using bucket: ${bucketName}`)
    return true
  } catch (error) {
    console.error('❌ Supabase connection error:', error.message)
    return false
  }
}

// Initialize connection test
testConnection()

module.exports = {
  supabase,
  testConnection
}
