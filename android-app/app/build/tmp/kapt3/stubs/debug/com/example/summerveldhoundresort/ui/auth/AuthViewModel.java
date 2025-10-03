package com.example.summerveldhoundresort.ui.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0013J$\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u00112\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0019J$\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\t0\u00112\u0006\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0013J<\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010\u001e\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u001f\u001a\u00020\u00172\u0006\u0010 \u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010!J<\u0010\"\u001a\b\u0012\u0004\u0012\u00020\t0\u00112\u0006\u0010#\u001a\u00020\u00172\u0006\u0010$\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010%\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010!R\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/example/summerveldhoundresort/ui/auth/AuthViewModel;", "Landroidx/lifecycle/ViewModel;", "firebaseUserRepo", "Lcom/example/summerveldhoundresort/db/implementations/firebaseUsersImpl;", "apiAuthRepo", "Lcom/example/summerveldhoundresort/network/repository/AuthRepository;", "(Lcom/example/summerveldhoundresort/db/implementations/firebaseUsersImpl;Lcom/example/summerveldhoundresort/network/repository/AuthRepository;)V", "_authTokens", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/summerveldhoundresort/network/models/AuthModels$AuthData;", "authTokens", "Landroidx/lifecycle/LiveData;", "getAuthTokens", "()Landroidx/lifecycle/LiveData;", "clearTokens", "", "getCurrentUserViaApi", "Lcom/example/summerveldhoundresort/db/AppResult;", "Lcom/example/summerveldhoundresort/network/models/AuthModels$UserProfile;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "login", "Lcom/example/summerveldhoundresort/db/entities/User;", "userLoginField", "", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loginViaApi", "email", "logoutViaApi", "register", "username", "phoneNumber", "creationDate", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerViaApi", "firstName", "lastName", "phone", "app_debug"})
public final class AuthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl firebaseUserRepo = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.summerveldhoundresort.network.repository.AuthRepository apiAuthRepo = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<com.example.summerveldhoundresort.network.models.AuthModels.AuthData> _authTokens = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.example.summerveldhoundresort.network.models.AuthModels.AuthData> authTokens = null;
    
    public AuthViewModel(@org.jetbrains.annotations.NotNull()
    com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl firebaseUserRepo, @org.jetbrains.annotations.NotNull()
    com.example.summerveldhoundresort.network.repository.AuthRepository apiAuthRepo) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.summerveldhoundresort.network.models.AuthModels.AuthData> getAuthTokens() {
        return null;
    }
    
    /**
     * Register user via API (recommended)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object registerViaApi(@org.jetbrains.annotations.NotNull()
    java.lang.String firstName, @org.jetbrains.annotations.NotNull()
    java.lang.String lastName, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.AuthModels.AuthData>> $completion) {
        return null;
    }
    
    /**
     * Login user via API (recommended)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loginViaApi(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.AuthModels.AuthData>> $completion) {
        return null;
    }
    
    /**
     * Legacy Firebase registration (kept for compatibility)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object register(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String creationDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    /**
     * Legacy Firebase login (kept for compatibility)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String userLoginField, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.db.entities.User>> $completion) {
        return null;
    }
    
    /**
     * Get current user via API
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCurrentUserViaApi(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<com.example.summerveldhoundresort.network.models.AuthModels.UserProfile>> $completion) {
        return null;
    }
    
    /**
     * Logout via API
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object logoutViaApi(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.summerveldhoundresort.db.AppResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    /**
     * Clear authentication tokens
     */
    public final void clearTokens() {
    }
}