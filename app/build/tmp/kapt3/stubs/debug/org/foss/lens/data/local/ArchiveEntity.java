package org.foss.lens.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b\u0087\b\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\'\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\u000b\u0010\u001b\u001a\u00020\u001c\u00a2\u0006\u0002\u0010\u001dJ\t\u0010\u001e\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006 "}, d2 = {"Lorg/foss/lens/data/local/ArchiveEntity;", "", "id", "", "payload", "", "format", "timestamp", "Ljava/time/Instant;", "(JLjava/lang/String;Ljava/lang/String;Ljava/time/Instant;)V", "getFormat", "()Ljava/lang/String;", "getId", "()J", "getPayload", "getTimestamp", "()Ljava/time/Instant;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toDomain", "error/NonExistentClass", "()Lerror/NonExistentClass;", "toString", "Companion", "app_debug"})
@androidx.room.Entity(tableName = "archive")
public final class ArchiveEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String payload = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String format = null;
    @org.jetbrains.annotations.NotNull
    private final java.time.Instant timestamp = null;
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.data.local.ArchiveEntity.Companion Companion = null;
    
    public ArchiveEntity(long id, @org.jetbrains.annotations.NotNull
    java.lang.String payload, @org.jetbrains.annotations.NotNull
    java.lang.String format, @org.jetbrains.annotations.NotNull
    java.time.Instant timestamp) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPayload() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getFormat() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.time.Instant getTimestamp() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final error.NonExistentClass toDomain() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.time.Instant component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final org.foss.lens.data.local.ArchiveEntity copy(long id, @org.jetbrains.annotations.NotNull
    java.lang.String payload, @org.jetbrains.annotations.NotNull
    java.lang.String format, @org.jetbrains.annotations.NotNull
    java.time.Instant timestamp) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lorg/foss/lens/data/local/ArchiveEntity$Companion;", "", "()V", "fromDomain", "Lorg/foss/lens/data/local/ArchiveEntity;", "codex", "error/NonExistentClass", "(Lerror/NonExistentClass;)Lorg/foss/lens/data/local/ArchiveEntity;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.data.local.ArchiveEntity fromDomain(@org.jetbrains.annotations.NotNull
        error.NonExistentClass codex) {
            return null;
        }
    }
}