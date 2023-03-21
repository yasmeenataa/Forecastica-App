package com.example.labmvvm.network



sealed class APIState {
    //class Success(val data:List<ProductModel>):APIState()
    class Failure(val msg:Throwable):APIState()
    object Loading:APIState()
}