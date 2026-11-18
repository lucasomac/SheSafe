package br.com.lucolimac.shesafe.core.domain.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Cross-feature domain events broadcast across the application via [AppEventBus].
 */
sealed class AppEvent {
    /**
     * Emitted when a user successfully completes authentication.
     */
    data object UserAuthenticated : AppEvent()

    /**
     * Emitted when the current user signs out or their session is cleared.
     */
    data object UserLoggedOut : AppEvent()

    /**
     * Emitted when the user's trusted emergency contacts list is created, updated, or deleted.
     */
    data object ContactsChanged : AppEvent()

    /**
     * Emitted when the user's emergency help request history changes.
     */
    data object HelpRequestsChanged : AppEvent()
}

/**
 * Publish-subscribe event bus for decoupled communication of [AppEvent] instances across features.
 */
interface AppEventBus {
    /**
     * Hot stream of [AppEvent] notifications emitted to active collectors.
     */
    val events: SharedFlow<AppEvent>

    /**
     * Publishes [event] to all active subscribers of [events].
     *
     * @param event The domain [AppEvent] to broadcast.
     */
    suspend fun emit(event: AppEvent)
}

/**
 * Default [MutableSharedFlow]-backed implementation of [AppEventBus] with a bounded drop-oldest buffer.
 */
class AppEventBusImpl : AppEventBus {
    /**
     * Backing mutable shared flow configured without replay and with extra capacity to prevent emitter suspension.
     */
    private val _events = MutableSharedFlow<AppEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Read-only [SharedFlow] exposing broadcast [AppEvent] instances.
     */
    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    /**
     * Emits [event] into the underlying shared flow.
     *
     * @param event The domain [AppEvent] to broadcast.
     */
    override suspend fun emit(event: AppEvent) {
        _events.emit(event)
    }
}
