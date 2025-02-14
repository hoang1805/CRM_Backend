package com.example.crm_backend.entities;

import com.example.crm_backend.entities.user.User;

public interface Releasable<T> {
    T release(User session_user);
    T release();
    T releaseCompact(User session_user);
    T releaseCompact();
}
