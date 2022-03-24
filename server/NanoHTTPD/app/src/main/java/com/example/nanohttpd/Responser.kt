package com.example.nanohttpd

class Responser<T>(val code: Int, val data: T, val msg: String) {
}