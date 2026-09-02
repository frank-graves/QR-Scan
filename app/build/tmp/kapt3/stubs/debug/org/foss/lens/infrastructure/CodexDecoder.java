package org.foss.lens.infrastructure;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lorg/foss/lens/infrastructure/CodexDecoder;", "", "()V", "reader", "Lcom/google/zxing/qrcode/QRCodeReader;", "decode", "Lorg/foss/lens/domain/Codex;", "image", "Landroidx/camera/core/ImageProxy;", "rotate", "", "Companion", "app_debug"})
public final class CodexDecoder {
    @org.jetbrains.annotations.NotNull
    private final com.google.zxing.qrcode.QRCodeReader reader = null;
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.infrastructure.CodexDecoder.Companion Companion = null;
    
    public CodexDecoder() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final org.foss.lens.domain.Codex decode(@org.jetbrains.annotations.NotNull
    androidx.camera.core.ImageProxy image, boolean rotate) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J-\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0000\u00a2\u0006\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lorg/foss/lens/infrastructure/CodexDecoder$Companion;", "", "()V", "rotateYPlane", "", "yData", "width", "", "height", "rotation", "rotateYPlane$app_debug", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final byte[] rotateYPlane$app_debug(@org.jetbrains.annotations.NotNull
        byte[] yData, int width, int height, int rotation) {
            return null;
        }
    }
}