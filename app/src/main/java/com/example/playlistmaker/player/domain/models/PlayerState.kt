package com.example.playlistmaker.player.domain.models

sealed interface PlayerState {
    object Default : PlayerState

    object Idle : PlayerState
    object Preparing : PlayerState
    data class Prepared(val duration: Int) : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Completed : PlayerState
}