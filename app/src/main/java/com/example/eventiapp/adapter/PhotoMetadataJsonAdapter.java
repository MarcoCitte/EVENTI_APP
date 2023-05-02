package com.example.eventiapp.adapter;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PhotoMetadataJsonAdapter implements JsonDeserializer<PhotoMetadata> {

    @Override
    public PhotoMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json != null) {
            JsonObject jsonObject = json.getAsJsonObject();
            String attribution = jsonObject.get("zza").getAsString();
            int height = jsonObject.get("zzb").getAsInt();
            int width = jsonObject.get("zzc").getAsInt();
            String photoReference = jsonObject.get("zzd").getAsString();

            return new PhotoMetadata() {

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(@NonNull Parcel dest, int flags) {

                }

                @Override
                public int getHeight() {
                    return height;
                }

                @Override
                public int getWidth() {
                    return width;
                }

                @NonNull
                @Override
                public String getAttributions() {
                    return attribution;
                }

                @NonNull
                @Override
                public String zza() {
                    return photoReference;
                }
            };
        }
        return null;
    }


}