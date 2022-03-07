<template>
  <div>
    <div>
      <GmapAutocomplete @place_changed="setPlace" />
    </div>
    <GmapMap
      :center="myCoordinates"
      :zoom="13"
      map-type-id="roadmap"
      style="width: 500px; height: 300px"
      ref="mapRef"
    >
      <GmapMarker :position="myCoordinates" />
    </GmapMap>
  </div>
</template>

<script>

export default {
  name: "MyMap",
  components: {
  },
  data() {
    return {
      myCoordinates: {
        lat: 0,
        lng: 0,
        name: "",
        formattedAddress: ""
      },
    };
  },
  created() {
    //get coordinate from browser
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(this.ShowPosition);
    }
  },
  methods: {
    setPlace(place) {
      var lat = place.geometry.location.lat();
      var lng = place.geometry.location.lng();
      var name = place.name;
      var formattedAddress = place.formatted_address;
      this.myCoordinates.lat = lat;
      this.myCoordinates.lng = lng;
      this.name = name;
      this.formattedAddress = formattedAddress;
    },
    ShowPosition(position) {
      this.myCoordinates.lat = position.coords.latitude;
      this.myCoordinates.lng = position.coords.longitude;
    },
  },
};
</script>
