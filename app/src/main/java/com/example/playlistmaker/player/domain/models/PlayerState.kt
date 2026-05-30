package com.example.playlistmaker.player.domain.models

sealed interface PlayerState {

    object Idle : PlayerState
    object Preparing : PlayerState
    data class Ready(val duration: Int) : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Completed : PlayerState
    data class Error(val message: String) : PlayerState
}