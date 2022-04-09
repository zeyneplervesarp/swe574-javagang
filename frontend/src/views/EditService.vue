<template>
  <div>
    <section class="section section-lg section-shaped overflow-hidden my-0">
      <div class="shape shape-style-3 shape-default shape-skew">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
      <div class="container shape-container py-0 pb-5">
        <div class="row row-grid justify-content-between align-items-center">
          <div class="col-lg-12">
            <br />
            <br />
            <h3 class="display-3 text-white">
              Create a Service
              <span class="text-white">contribute to socialhub</span>
            </h3>
            <p class="lead text-white">
              You can create services to contribute to the socialhub community.
              You gain a time balance depending on the duration of the service
              where you can participate in other socialhub services.
            </p>
            <div class="container mb-5">
              <!-- Inputs -->
              <form role="form">
                <br />
                <div class="col-lg-12">
                  <base-input
                    placeholder="Header"
                    addon-left-icon="ni ni-align-left-2"
                    v-model="serviceData.header"
                  ></base-input>
                </div>
                <div class="col-lg-12">
                  <textarea
                    class="form-control form-control-alternative"
                    v-model="serviceData.description"
                    rows="3"
                    placeholder="Write a description of the service here ..."
                  ></textarea>
                  <br />
                </div>
                <div class="col-lg-12">
                  <date-picker
                    input-class="form-control"
                    v-model="serviceData.time"
                    type="datetime"
                  ></date-picker>
                </div>
                <br />
                <div class="col-lg-12">
                  <input
                    type="number"
                    class="form-control"
                    v-model="serviceData.quota"
                    placeholder="Quota"
                  />
                </div>
                <br />
                <div class="col-lg-12">
                  <input
                    type="number"
                    class="form-control"
                    v-model="serviceData.minutes"
                    placeholder="Credits"
                  />
                  <br />
                </div>
                <div class="col-lg-12">
                  <multiselect
                    v-model="serviceData.serviceTags"
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
                </div>
                <br />
                <div class="col-lg-12">
                  <div class="form-group">
                    <GmapAutocomplete
                      class="form-control"
                      addon-left-icon="ni ni-pin-3"
                      :placeholder="serviceData.location"
                      @place_changed="setPlace"
                    />
                  </div>
                  <div class="text-center">
                    <base-button v-if="serviceData.location != ''" type="secondary"
                      ><GmapMap
                        :center="coordinates"
                        :zoom="13"
                        map-type-id="roadmap"
                        style="width: 500px; height: 300px"
                        ref="mapRef"
                        
                      >
                        <GmapMarker :position="coordinates" /> </GmapMap
                    ></base-button>
                  </div>
                </div>
                <div class="text-center">
                  <base-button
                    v-on:click="SendService"
                    type="success"
                    class="my-4"
                    >Edit</base-button
                  >
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
    <section class="section pb-0 section-components">
      <div class="container mb-5 bg-secondary"></div>
    </section>
  </div>
</template>
<script>
import apiRegister from "../api/register";
import DatePicker from "vue2-datepicker";
import Multiselect from "vue-multiselect";
import MyMap from "./components/Map.vue";
import register from '../api/register';

export default {
  components: {
    DatePicker,
    Multiselect,
    MyMap,
  },
  mounted() {
    this.GetGeoLocation();
    this.GetTags();
    this.GetService();
    this.GetUserDetails();
  },
  data() {
    return {
      serviceData: {
        id:"",
        location: "",
        time: "",
        header: "",
        minutes: "",
        description: "",
        quota: "",
        createdUserIdId: "",
        latitude: "",
        longitude: "",
        serviceTags: [],
        timeString: "",
        createdUserName: "",
        status: "",
        datePassed: false,
        participantUserList: [],
      },
      userData: {
        ownsService: "",
      },
      coordinates: {
        lat: 0,
        lng: 0,
      },
      tags: [],
    };
  },
  methods: {
     GetService() {
      var id = this.$route.params.service_id;
      apiRegister.GetService(id).then((r) => {
        this.serviceData.location = r.location;
        this.serviceData.time = r.time;
        this.serviceData.timeString = r.timeString;
        this.serviceData.header = r.header;
        this.serviceData.minutes = r.minutes;
        this.serviceData.description = r.description;
        this.serviceData.quota = r.quota;
        this.serviceData.createdUserIdId = r.createdUserIdId;
        this.serviceData.createdUserName = r.createdUserName;
        this.serviceData.serviceTags = r.serviceTags;
        this.serviceData.attendingUserCount = r.attendingUserCount;
        this.serviceData.status = r.status;
        this.serviceData.participantUserList = r.participantUserList;
        this.serviceData.datePassed = r.showServiceOverButton;
        this.coordinates.lat = r.latitude;
        this.coordinates.lng = r.longitude;
        this.serviceData.id = r.id;
      });
    },
      GetUserDetails() {
      var id = this.$route.params.service_id;
      apiRegister.GetUserServiceDetails(id).then((r) => {
        this.userData.hasServiceRequest = r.hasServiceRequest;
        this.userData.ownsService = r.ownsService;
        this.userData.attendsService = r.attendsService;
      });
    },
    SendService() {
      console.log("Send service started");
      apiRegister.CreateService(this.serviceData).then((r) => {
        console.log("Send service ok");
        document.location.href = "#/service/" + r;
      });
    },
    GetGeoLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(this.ShowPosition);
      }
    },
    ShowPosition(position) {
      this.serviceData.latitude = position.coords.latitude;
      this.serviceData.longitude = position.coords.longitude;
      this.coordinates.lat = position.coords.latitude;
      this.coordinates.lng = position.coords.longitude;
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
      this.serviceData.latitude = lat;
      this.serviceData.longitude = lng;
      this.serviceData.location = formattedAddress;
      this.coordinates.lat = lat;
      this.coordinates.lng = lng;
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
