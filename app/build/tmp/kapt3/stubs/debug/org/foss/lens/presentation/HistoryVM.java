package org.foss.lens.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u000f\u001a\u00020\u000eR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0010"}, d2 = {"Lorg/foss/lens/presentation/HistoryVM;", "Landroidx/lifecycle/ViewModel;", "archive", "Lorg/foss/lens/domain/Archive;", "(Lorg/foss/lens/domain/Archive;)V", "_entries", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lorg/foss/lens/domain/Codex;", "entries", "Lkotlinx/coroutines/flow/StateFlow;", "getEntries", "()Lkotlinx/coroutines/flow/StateFlow;", "clear", "", "load", "app_debug"})
public final class HistoryVM extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final org.foss.lens.domain.Archive archive = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<org.foss.lens.domain.Codex>> _entries = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<org.foss.lens.domain.Codex>> entries = null;
    
    public HistoryVM(@org.jetbrains.annotations.NotNull
    org.foss.lens.domain.Archive archive) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<org.foss.lens.domain.Codex>> getEntries() {
        return null;
    }
    
    public final void load() {
    }
    
    public final void clear() {
    }
}