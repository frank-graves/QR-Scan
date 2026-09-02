package org.foss.lens.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0002\u000f\u0010B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\nH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lorg/foss/lens/presentation/HistoryAdapter;", "", "()V", "dateFormat", "Ljava/text/SimpleDateFormat;", "onBindViewHolder", "", "holder", "Lorg/foss/lens/presentation/HistoryAdapter$ViewHolder;", "position", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "DiffCallback", "ViewHolder", "app_debug"})
public final class HistoryAdapter {
    @org.jetbrains.annotations.NotNull
    private final java.text.SimpleDateFormat dateFormat = null;
    
    public HistoryAdapter() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public org.foss.lens.presentation.HistoryAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    org.foss.lens.presentation.HistoryAdapter.ViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c0\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a8\u0006\t"}, d2 = {"Lorg/foss/lens/presentation/HistoryAdapter$DiffCallback;", "", "()V", "areContentsTheSame", "", "oldItem", "Lorg/foss/lens/domain/Codex;", "newItem", "areItemsTheSame", "app_debug"})
    public static final class DiffCallback {
        @org.jetbrains.annotations.NotNull
        public static final org.foss.lens.presentation.HistoryAdapter.DiffCallback INSTANCE = null;
        
        private DiffCallback() {
            super();
        }
        
        public boolean areItemsTheSame(@org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex oldItem, @org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex newItem) {
            return false;
        }
        
        public boolean areContentsTheSame(@org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex oldItem, @org.jetbrains.annotations.NotNull
        org.foss.lens.domain.Codex newItem) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u000b"}, d2 = {"Lorg/foss/lens/presentation/HistoryAdapter$ViewHolder;", "", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "text1", "Landroid/widget/TextView;", "getText1", "()Landroid/widget/TextView;", "text2", "getText2", "app_debug"})
    public static final class ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView text1 = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView text2 = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View view) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getText1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getText2() {
            return null;
        }
    }
}