package org.foss.lens.domain;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0003\u0004\u0005\u0006B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0004\u0007\b\t\n\u00a8\u0006\u000b"}, d2 = {"Lorg/foss/lens/domain/ScanState;", "", "()V", "Detecting", "Error", "Idle", "Success", "Lorg/foss/lens/domain/ScanState$Detecting;", "Lorg/foss/lens/domain/ScanState$Error;", "Lorg/foss/lens/domain/ScanState$Idle;", "Lorg/foss/lens/domain/ScanState$Success;", "app_debug"})
public abstract class ScanState {
    
    private ScanState() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lorg/foss/lens/domain/ScanState$Detecting;", "Lorg/foss/lens/domain/ScanState;", "()V", "app_debug"})
    public static final class Detecting extends org.foss.lens.domain.ScanState {
        @org.jetbrains.annotations.NotNull
        public static final org.foss.lens.domain.ScanState.Detecting INSTANCE = null;
        
        private Detecting() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lorg/foss/lens/domain/ScanState$Error;", "Lorg/foss/lens/domain/ScanState;", "cause", "", "message", "", "(Ljava/lang/Throwable;Ljava/lang/String;)V", "getCause", "()Ljava/lang/Throwable;", "getMessage", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
    public static final class Error extends org.foss.lens.domain.ScanState {
        @org.jetbrains.annotations.NotNull
        private final java.lang.Throwable cause = null;
        @org.jetbrains.annotations.Nullable
        private final java.lang.String message = null;
        
        public Error(@org.jetbrains.annotations.NotNull
        java.lang.Throwable cause, @org.jetbrains.annotations.Nullable
        java.lang.String message) {
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.Throwable getCause() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.Throwable component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.domain.ScanState.Error copy(@org.jetbrains.annotations.NotNull
        java.lang.Throwable cause, @org.jetbrains.annotations.Nullable
        java.lang.String message) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lorg/foss/lens/domain/ScanState$Idle;", "Lorg/foss/lens/domain/ScanState;", "()V", "app_debug"})
    public static final class Idle extends org.foss.lens.domain.ScanState {
        @org.jetbrains.annotations.NotNull
        public static final org.foss.lens.domain.ScanState.Idle INSTANCE = null;
        
        private Idle() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lorg/foss/lens/domain/ScanState$Success;", "Lorg/foss/lens/domain/ScanState;", "codex", "Lorg/foss/lens/domain/Codex;", "(Lorg/foss/lens/domain/Codex;)V", "getCodex", "()Lorg/foss/lens/domain/Codex;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Success extends org.foss.lens.domain.ScanState {
        @org.jetbrains.annotations.NotNull
        private final org.foss.lens.domain.Codex codex = null;
        
        public Success(@org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex codex) {
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.domain.Codex getCodex() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.domain.Codex component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.domain.ScanState.Success copy(@org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex codex) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}