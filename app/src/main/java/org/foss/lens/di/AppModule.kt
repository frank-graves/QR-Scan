// org/foss/lens/di/AppModule.kt
package org.foss.lens.di

import android.content.Context
import io.ktor.client.HttpClient
import org.foss.lens.BuildConfig
import org.foss.lens.data.AssetLocal
import org.foss.lens.data.ScanHistoryLocal
import org.foss.lens.data.local.AssetDao
import org.foss.lens.data.local.LensDatabase
import org.foss.lens.data.local.ScanHistoryDao
import org.foss.lens.data.sync.AssetSyncer
import org.foss.lens.domain.AssetClassifier
import org.foss.lens.domain.AssetRepository
import org.foss.lens.domain.JsonAssetClassifier
import org.foss.lens.infrastructure.CodexDecoder
import org.foss.lens.infrastructure.NetworkMonitor
import org.foss.lens.remote.AssetSyncGateway
import org.foss.lens.remote.MockApiGateway
import org.foss.lens.ui.PendingScanHolder
import org.foss.lens.ui.screens.AssetDetailViewModel
import org.foss.lens.ui.screens.ConfirmAssetViewModel
import org.foss.lens.ui.screens.HistoryViewModel
import org.foss.lens.ui.screens.InventoryViewModel
import org.foss.lens.ui.screens.ManualAssetViewModel
import org.foss.lens.ui.screens.ScannerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // --- Android / infraestructura ---
    single { LensDatabase.getInstance(get<Context>()) }
    single<ScanHistoryDao> { get<LensDatabase>().scanHistoryDao() }
    single<AssetDao> { get<LensDatabase>().assetDao() }

    single<CodexDecoder> { CodexDecoder() }
    single<NetworkMonitor> { NetworkMonitor(get<Context>()) }

    // --- Datos ---
    single<ScanHistoryLocal> { ScanHistoryLocal(get()) }
    single<AssetRepository> { AssetLocal(get()) }

    // --- Clasificación ---
    single<AssetClassifier> { JsonAssetClassifier() }

    // --- Red / sync ---
    single<HttpClient> { MockApiGateway.httpClient() }
    single<AssetSyncGateway> {
        MockApiGateway(BuildConfig.MOCKAPI_BASE_URL, get())
    }
    single<AssetSyncer> { AssetSyncer(get(), get()) }

    // --- Estado compartido UI ---
    single<PendingScanHolder> { PendingScanHolder() }

    // --- ViewModels ---
    viewModel { ScannerViewModel(get(), get(), get(), get()) }
    viewModel { ConfirmAssetViewModel(get(), get(), get()) }
    viewModel { ManualAssetViewModel(get(), get()) }
    viewModel { InventoryViewModel(get(), get()) }
    viewModel { AssetDetailViewModel(get(), get(), get()) }
    viewModel { HistoryViewModel(get()) }
}
