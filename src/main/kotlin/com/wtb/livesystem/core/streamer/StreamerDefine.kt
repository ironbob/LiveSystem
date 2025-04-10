package com.wtb.livesystem.core.streamer

import com.google.gson.Gson

data class StreamerDefine(var name: String,
                          var type:String,
                          var baseParameter: BaseParameter,
                          var emotions:List<EmotionDefine>,

                          ) {

    companion object{
        fun fromJson(json: String): StreamerDefine {
            return Gson().fromJson<StreamerDefine>(json, StreamerDefine::class.java)
        }
    }
}

data class BaseParameter(var speedRate:Float,
                         var volume:Float,
                         var breakProbability:Float,
                         var breakDurationRange:IntRange,
                         var breathProbability:Float,
                         var breathDurationRange:IntRange,
                         var fillWordsProbability:Float,
    var speechErrorProbability:Float)

data class EmotionDefine(var name: String,
    var bias: ParameterBias
)

data class ParameterBias(var speedRateBias:Float,
                         var volumeBias:Float,
                         var breakProbabilityBias:Float,
                         var breakDurationRangeBias:IntRange,
                         var breathProbabilityBias:Float,
                        var breathDurationRangeBias:IntRange,
                         var fillWordsProbabilityBias:Float,
    var speechErrorProbabilityBias:Float)