package com.example.baki_tracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.coroutineScope
import com.example.baki_tracker.dependencyInjection.MainActivityComponent
import com.example.baki_tracker.dependencyInjection.applicationComponent
import com.example.baki_tracker.dependencyInjection.create
import com.example.baki_tracker.repository.IGoogleRepository
import com.example.baki_tracker.ui.theme.BakiTrackerTheme
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

class MainActivity : ComponentActivity() {


    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    handleAuthResponseIntent(it)
                }
            }
        }

    lateinit var authorizationService: AuthorizationService

    private lateinit var googleRepository: IGoogleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authorizationService = AuthorizationService(this)
        val mainActivityComponent = MainActivityComponent::class.create(applicationComponent)
        googleRepository = mainActivityComponent.googleRepository
        setContent {
            BakiTrackerTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        val scope = rememberCoroutineScope()
                        LaunchedEffect(true) {
                            scope.launch {
                                googleRepository.authorizationRequest.collect {
                                    if (it != null) {
                                        val intent =
                                            authorizationService.getAuthorizationRequestIntent(it)
                                        getAuthResponse.launch(intent)
                                    }
                                }
                            }
                        }

                        //this is the entrypoint of the application
                        mainActivityComponent.rootContainer()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authorizationService.dispose()
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest =
            AuthorizationResponse.fromIntent(intent)?.createTokenExchangeRequest()

        when {
            exception != null -> {}//googleRepository.onAuthCodeFailed(exception)
            tokenExchangeRequest != null -> lifecycle.coroutineScope.launch {
                googleRepository.onAuthCodeReceived(authorizationService, tokenExchangeRequest)
            }
        }
    }
}