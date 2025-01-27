package com.example.baki_tracker.repository

import android.net.Uri
import androidx.core.net.toUri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import kotlin.coroutines.suspendCoroutine

object AppAuth {

    val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        // null, // registration endpoint
        // Uri.parse(AuthConfig.END_SESSION_URI)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.CALLBACK_URL.toUri()

        return AuthorizationRequest.Builder(
            serviceConfiguration, AuthConfig.CLIENT_ID, AuthConfig.RESPONSE_TYPE, redirectUri
        ).setScope(AuthConfig.SCOPE).setPrompt("consent")
            .setAdditionalParameters(mapOf("access_type" to "offline"))
            .build()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return EndSessionRequest.Builder(serviceConfiguration)
            .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri()).build()
    }

    fun getRefreshTokenRequest(refreshToken: String): TokenRequest {
        return TokenRequest.Builder(
            serviceConfiguration, AuthConfig.CLIENT_ID
        ).setGrantType(GrantTypeValues.REFRESH_TOKEN).setScopes(AuthConfig.SCOPE)
            .setRefreshToken(refreshToken)
            .setAdditionalParameters(
                mapOf(
                    "client_secret" to AuthConfig.CLIENT_SECRET,
                    "access_type" to "offline"
                ) // Add the client_secret here
            )
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokenResponse? {

        val request = TokenRequest.Builder(serviceConfiguration, AuthConfig.CLIENT_ID)
        request.setRefreshToken(tokenRequest.refreshToken)
        request.setGrantType(tokenRequest.grantType)
        request.setScopes(AuthConfig.SCOPE)
        request.setAuthorizationCode(tokenRequest.authorizationCode)
        request.setCodeVerifier(tokenRequest.codeVerifier)
        request.setNonce(tokenRequest.nonce)
        request.setRedirectUri(tokenRequest.redirectUri)
        request.setAdditionalParameters(
            mapOf(
                "client_secret" to AuthConfig.CLIENT_SECRET,
                "access_type" to "offline"
            ) // Add the client_secret here
        )

        return suspendCoroutine { continuation ->
            authService.performTokenRequest(request.build()) { response, ex ->
                when {
                    response != null -> {
                        continuation.resumeWith(Result.success(response))
                    }
                    ex != null -> {
                        continuation.resumeWith(Result.failure(ex))
                    }
                    else -> error("unreachable")
                }
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretBasic("")
    }

    object AuthConfig {
        const val AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth"
        const val TOKEN_URI = "https://oauth2.googleapis.com/token"
        const val END_SESSION_URI = "" // to be defined
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        const val SCOPE = "https://www.googleapis.com/auth/calendar"

        const val CLIENT_ID =
            "330993400404-7hnedv0tcg08cnq8ncuc8sqif1o7sfl7.apps.googleusercontent.com"
        const val CLIENT_SECRET = "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"
        const val CALLBACK_URL = "https://bakitracker.com/callback"
        const val LOGOUT_CALLBACK_URL = "https://bakitracker.com/callback"
    }
}