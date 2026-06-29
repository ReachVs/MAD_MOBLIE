package com.example.mad_final.data.remote.interceptor

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class MockInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val uri = request.url.toUri().toString()
        
        var code = 200
        val responseString = when {
            uri.contains("auth/login") -> {
                val body = request.body
                // Simple check for mock credentials
                // In a real app, this would be handled by the server
                val requestString = if (body != null) {
                    val buffer = okio.Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } else ""
                
                if (requestString.contains("admin@apex.com") && requestString.contains("admin123")) {
                    """{"token": "admin_jwt_token", "userId": "admin_001", "email": "admin@apex.com", "name": "System Admin", "role": "ADMIN"}"""
                } else if (requestString.contains("user@apex.com") && requestString.contains("user123")) {
                    """{"token": "user_jwt_token", "userId": "user_001", "email": "user@apex.com", "name": "Apex Rider", "role": "CUSTOMER"}"""
                } else {
                    code = 401
                    """{"message": "Invalid credentials. Use admin@apex.com/admin123 or user@apex.com/user123"}"""
                }
            }
            uri.contains("auth/register") -> {
                """{"token": "mock_jwt_token_123", "userId": "user_001", "email": "rider@apex.com", "name": "Apex Rider"}"""
            }
            uri.contains("motorcycles") && chain.request().method == "GET" -> {
                """[
                    {"id": "1", "brand": "Ducati", "model": "Panigale V4", "year": 2024, "pricePerDay": 250.0, "availability": true, "imageUrl": "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87", "description": "The ultimate expression of Ducati racing DNA.", "type": "Sport"},
                    {"id": "2", "brand": "BMW", "model": "S1000RR", "year": 2024, "pricePerDay": 230.0, "availability": true, "imageUrl": "https://images.unsplash.com/photo-1622185135505-2d795003994a", "description": "Designed for the track, mastered for the road.", "type": "Sport"},
                    {"id": "3", "brand": "Triumph", "model": "Bonneville T120", "year": 2023, "pricePerDay": 150.0, "availability": true, "imageUrl": "https://images.unsplash.com/photo-1558981403-c5f9899a28bc", "description": "A timeless icon of British motorcycling.", "type": "Classic"}
                ]"""
            }
            uri.contains("services") -> {
                """[
                    {
                        "id": "s1",
                        "title": "Full Performance Tune",
                        "price": "$450",
                        "description": "Comprehensive ECU remapping and dyno testing for maximum power delivery.",
                        "imageUrl": "tuning.png",
                        "tags": ["ECU Remap", "Dyno", "Fuel Map"],
                        "category": "TUNING PERFORMANCE"
                    },
                    {
                        "id": "s2",
                        "title": "Annual Maintenance",
                        "price": "$150",
                        "description": "Standard yearly checkup including oil change, filter replacement, and safety inspection.",
                        "imageUrl": "maintenance.png",
                        "tags": ["Oil Change", "Safety Check"],
                        "category": "MAINTENANCE SERVICES"
                    }
                ]"""
            }
            uri.contains("motorcycles") -> {
                // Return a simple success message for POST, PUT, DELETE
                """{"status": "success", "message": "Operation completed successfully"}"""
            }
            else -> "{}"
        }

        return Response.Builder()
            .code(code)
            .message(if (code == 200) "OK" else "Unauthorized")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .addHeader("content-type", "application/json")
            .body(responseString.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
