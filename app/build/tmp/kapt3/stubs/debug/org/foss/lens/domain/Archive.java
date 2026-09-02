package org.foss.lens.domain;

/**
 * Persistence seam for scanned payloads.
 *
 * Implementations are expected to be suspend-friendly and local-only:
 * the archive is a disposable history, never synced off-device.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J\u0011\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J\u0019\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000bJ\u0019\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u0004H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000e\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u000f"}, d2 = {"Lorg/foss/lens/domain/Archive;", "", "all", "", "Lorg/foss/lens/domain/Codex;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clear", "", "delete", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "save", "entry", "(Lorg/foss/lens/domain/Codex;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface Archive {
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object save(@org.jetbrains.annotations.NotNull
    org.foss.lens.domain.Codex entry, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object all(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<org.foss.lens.domain.Codex>> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object delete(long id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object clear(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}