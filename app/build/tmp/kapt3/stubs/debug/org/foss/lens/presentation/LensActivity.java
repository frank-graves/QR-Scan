package org.foss.lens.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\u0012\u0010\u0011\u001a\u00020\u00102\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\u0010\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lorg/foss/lens/presentation/LensActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lorg/foss/lens/databinding/ActivityLensBinding;", "historyAdapter", "Lorg/foss/lens/presentation/HistoryAdapter;", "historyVM", "Lorg/foss/lens/presentation/HistoryVM;", "permissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "scanVM", "Lorg/foss/lens/presentation/ScanVM;", "checkAndRequestPermissions", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "updateUiForState", "state", "Lorg/foss/lens/domain/ScanState;", "app_debug"})
public final class LensActivity extends androidx.appcompat.app.AppCompatActivity {
    private org.foss.lens.databinding.ActivityLensBinding binding;
    private org.foss.lens.presentation.ScanVM scanVM;
    private org.foss.lens.presentation.HistoryVM historyVM;
    private org.foss.lens.presentation.HistoryAdapter historyAdapter;
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> permissionLauncher = null;
    
    public LensActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkAndRequestPermissions() {
    }
    
    private final void updateUiForState(org.foss.lens.domain.ScanState state) {
    }
}