<template>
  <section class="section section-shaped section-lg my-0">
    <div class="shape shape-style-1 bg-gradient-default">
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
    </div>
    <div class="container pt-lg-md">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <card
            type="secondary"
            shadow
            header-classes="bg-white pb-5"
            body-classes="px-lg-5 py-lg-5"
            class="border-0"
          >
            <template>
              <form role="form">
                <base-input
                  alternative
                  class="mb-3"
                  placeholder="Username"
                  v-model="registerInputs.username"
                  addon-left-icon="ni ni-hat-3"
                >
                </base-input>
                <base-input
                  alternative
                  class="mb-3"
                  placeholder="Email"
                  v-model="registerInputs.email"
                  addon-left-icon="ni ni-email-83"
                >
                </base-input>
                <base-input
                  alternative
                  class="mb-3"
                  placeholder="Short Bio"
                  v-model="registerInputs.bio"
                  addon-left-icon="ni ni-bell-55"
                >
                </base-input>
                <base-input
                  alternative
                  type="password"
                  v-model="registerInputs.password"
                  placeholder="Password"
                  addon-left-icon="ni ni-lock-circle-open"
                >
                </base-input>
                <multiselect
                  v-model="registerInputs.userTags"
                  :options="tags"
                  :multiple="true"
                  :close-on-select="false"
                  :show-labels="false"
                  :taggable="true" 
                  @tag="addTag"
                  placeholder="Pick a tag or enter a new one"
                  label="name"
                  track-by="id"
                ></multiselect>
                <br />
                <div class="justify-content-center">
                  <div class="form-group">
                    <GmapAutocomplete
                      class="form-control"
                      @place_changed="setPlace"
                    />
                  </div>
                  <div class="text-center">
                    <base-button v-if="registerInputs.formattedAddress != ''">
                      <GmapMap
                        :center="coordinates"
                        :zoom="13"
                        map-type-id="roadmap"
                        style="width: 500px; height: 300px"
                        ref="mapRef"
                      >
                        <GmapMarker :position="coordinates" />
                      </GmapMap>
                    </base-button>
                  </div>
                </div>
                <div class="text-center">
                  <base-button
                    v-on:click="SendRegister"
                    type="primary"
                    class="my-4"
                    >Create account</base-button
                  >
                </div>
              </form>
            </template>
          </card>
          <div class="row mt-3"></div>
        </div>
      </div>
    </div>
  </section>
</template>
<script>
import apiRegister from "@/api/register";
import Multiselect from "vue-multiselect";
import MyMap from "./components/Map.vue";

export default {
  components: {
    Multiselect,
    MyMap,
  },
  data() {
    return {
      registerInputs: {
        username: "",
        email: "",
        bio: "",
        password: "",
        userTags: [],
        latitude: 0,
        longitude: 0,
        locationName: "",
        formattedAddress: "",
      },
      tags: [],
      coordinates: {
        lat: 0,
        lng: 0,
      },
    };
  },
  mounted() {
    this.GetTags();
  },
  created() {
    //get coordinate from browser
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(this.ShowPosition);
    }
  },
  methods: {
    SendRegister() {
      apiRegister.Register(this.registerInputs).then((r) => {
        if (r.jwt) {
          localStorage.setItem("token", JSON.stringify(r.jwt));
          document.location.href = "../";
        }
      });
    },
    GetTags() {
      apiRegister.GetTags().then((r) => {
        this.tags = r;
      });
    },
    setPlace(place) {
      var lat = place.geometry.location.lat();
      var lng = place.geometry.location.lng();
      var name = place.name;
      var formattedAddress = place.formatted_address;
      this.registerInputs.latitude = lat;
      this.registerInputs.longitude = lng;
      this.registerInputs.locationName = name;
      this.registerInputs.formattedAddress = formattedAddress;
      this.coordinates.lat = lat;
      this.coordinates.lng = lng;
    },
    ShowPosition(position) {
      this.registerInputs.latitude = position.coords.latitude;
      this.registerInputs.longitude = position.coords.longitude;
      this.coordinates.lat = position.coords.latitude;
      this.coordinates.lng = position.coords.longitude;
    },
    addTag (newTag) {
      const tag = {
        name: newTag
      }
      register.AddTag(tag);
    }
  },
};
</script>
<style>
</style>
