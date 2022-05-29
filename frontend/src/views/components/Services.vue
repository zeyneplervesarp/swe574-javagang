<template>
  <div class="container">
    <div class="row justify-content-center">
      <div v-if="this.filter == 'all'" class="row pull-left">
        <base-dropdown>
          <base-button
            slot="title"
            type="warning"
            class="dropdown-toggle float-right"
          >
            Sort By
          </base-button>
          <a class="dropdown-item" href="#" v-on:click="SortBy('distanceAsc')"
            >Distance
            <span class="btn-inner--icon">
              <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
          ></a>
          <a
            class="dropdown-item"
            href="#"
            v-on:click="SortBy('serviceDateAsc')"
            >Service Date
            <span class="btn-inner--icon">
              <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
          ></a>
          <a
            class="dropdown-item"
            href="#"
            v-on:click="SortBy('serviceDateDesc')"
            >Service Date
            <span class="btn-inner--icon">
              <i class="fa fa- fa-sort-amount-desc mr-2"></i> </span
          ></a>
          <a
            class="dropdown-item"
            href="#"
            v-on:click="SortBy('createdDateAsc')"
            >Created Date
            <span class="btn-inner--icon">
              <i class="fa fa- fa-sort-amount-asc mr-2"></i> </span
          ></a>
          <a
            class="dropdown-item"
            href="#"
            v-on:click="SortBy('createdDateDesc')"
            >Created Date
            <span class="btn-inner--icon">
              <i class="fa fa- fa-sort-amount-desc mr-2"></i> </span
          ></a>
        </base-dropdown>
      </div>
      <span v-if="this.sortByDistance==true">___</span>
      <div v-if="this.sortByDistance==true" class="row pull-left" >
        <div class="form-group">
        <GmapAutocomplete
            class="form-control"
            @place_changed="setPlace"
            placeholder="Enter Location"
            :value="searchlocation"
        />
      </div>
      </div>
      <div class="col-lg-12 pt-100" v-if="nestedServiceArray.length > 0">
        <div v-if="this.filter == 'featured'">
          <p class="lead text-white">This week's featured services!</p>
        </div>
        <div v-if="this.filter == 'recommended'">
          <p class="lead text-white">Recommended services near you!</p>
        </div>
        <div
          v-for="(serviceArray, index) in nestedServiceArray"
          :key="index"
          class="row row-grid"
        >
          <div
            v-for="(service, index) in serviceArray"
            :key="index"
            class="col-lg-4"
          >
            <card class="border-0" hover shadow body-classes="py-5">
              <icon
                v-bind:type="GetClass(index)"
                v-bind:name="GetIcon(index)"
                rounded
                class="mb-4"
              >
              </icon>
              <h6 v-bind:class="GetTextClass(index)">{{ service.header }}</h6>
              <div>
                <span class="description mt-3"
                  >{{ GetFormattedDate(service.time) }}
                </span>

                <badge
                  v-if="IsDateBeforeToday(service.time)"
                  v-bind:type="danger"
                  rounded
                >
                  PAST SERVICE
                </badge>
              </div>
              <p class="description mt-3">
                {{ service.description }}
              </p>
              <div>
                <badge v-bind:type="GetClass(index)" rounded
                  >{{ service.hours }} credits
                </badge>
                <badge
                  v-if="service.distanceToUserString != ''"
                  v-bind:type="GetClass(index)"
                  rounded
                  >{{ service.distanceToUserString }}</badge
                >
                <badge v-else v-bind:type="GetClass(index)" rounded
                  >online</badge
                >
              </div>
              <base-button
                tag="a"
                v-if="isLoggedIn"
                :href="'#/service/' + service.id"
                v-bind:type="GetClass(index)"
                class="mt-4"
              >
                Learn more
              </base-button>
              <base-button
                tag="a"
                v-if="!isLoggedIn"
                :href="'#/register/'"
                v-bind:type="GetClass(index)"
                class="mt-4"
              >
                Learn More
              </base-button>
            </card>
          </div>
        </div>
        <div class="row justify-content-center">
          <base-button
            @click="LoadMore()"
            v-bind:type="GetClass(0)"
            class="mt-4"
            v-if="this.filter == 'all' || this.filter == 'recommended'"
          >
            Load more services
          </base-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Hero from "./Hero";
import apiRegister from "@/api/register";
import moment from "moment";
import BaseDropdown from "@/components/BaseDropdown";

export default {
  name: "services",
  data() {
    return {
      serviceResult: [],
      nestedServiceArray: [],
      getOngoingOnly: true,
      isLoggedIn: false,
      nextUrl: "",
      sortByDistance: false,
    };
  },
  mounted() {
    this.GetServices();
    let token = JSON.parse(localStorage.getItem("token"));

    if (token) {
      this.isLoggedIn = true;
    } else {
      this.isLoggedIn = false;
    }
  },
  methods: {
    IsDateBeforeToday(date) {
      var today = new Date();
      var serviceDate = new Date(date);
      return today > serviceDate;
    },
    GetFormattedDate(date) {
      return moment(date).format("YYYY-MM-DD h:mm");
    },
    GetServices() {
      if (this.filter == "featured") {
        apiRegister.GetFeaturedServices().then((response) => {
          this.serviceResult = response;
          this.nestedServiceArray = this.SplitList();
        });
      } else if (this.filter == "recommended") {
        apiRegister.GetRecommendedServices().then((response) => {
          this.serviceResult = response.items;
          this.nestedServiceArray = this.SplitList();
          this.nextUrl = response.nextPage;
        });
      } else {
        apiRegister
          .GetAllServices(this.getOngoingOnly, this.filter)
          .then((response) => {
            this.serviceResult = response.items;
            this.nextUrl = response.nextPage;
            this.nestedServiceArray = this.SplitList();
          });
      }
    },
    SplitList() {
      var array = this.serviceResult;
      var listOfArrays = [];

      var i,
        j,
        temporary,
        chunk = 3;
      for (i = 0, j = array.length; i < j; i += chunk) {
        temporary = array.slice(i, i + chunk);
        listOfArrays.push(temporary);
      }
      return listOfArrays;
    },
    GetClass(index) {
      var i = index + (1 % 3);
      if (i == 1) {
        return "primary";
      } else if (i == 2) {
        return "success";
      } else {
        return "warning";
      }
    },
    GetIcon(index) {
      var i = index + (1 % 3);
      if (i == 1) {
        return "ni ni-check-bold";
      } else if (i == 2) {
        return "ni ni-istanbul";
      } else {
        return "ni ni-planet";
      }
    },
    GetTextClass(index) {
      var i = index + (1 % 3);
      if (i == 1) {
        return "text-primary text-uppercase";
      } else if (i == 2) {
        return "text-success text-uppercase";
      } else {
        return "text-warning text-uppercase";
      }
    },
    SortBy(sortBy) {
      apiRegister
        .GetAllServicesSorted(
          this.getOngoingOnly,
          this.filter,
          sortBy,
          false,
          18
        )
        .then((response) => {
          this.serviceResult = response.items;
          this.nestedServiceArray = this.SplitList();
          this.nextUrl = response.nextPage;
        });
        if(sortBy == 'distanceAsc'){
          this.sortByDistance = true;
          //this.searchlocation = null;
        }else{
          this.sortByDistance = false;
        }
    },
    LoadMore() {
      apiRegister.GetUrl(this.nextUrl).then((response) => {
        var merged = [...this.serviceResult, ...response.items];
        this.serviceResult = merged;
        this.nextUrl = response.nextPage;
        this.nestedServiceArray = this.SplitList();
      });
    },
    setPlace(place) {
      var lat = place.geometry.location.lat();
      var lng = place.geometry.location.lng();
      var name = place.name;
      var formattedAddress = place.formatted_address;
      
      apiRegister
        .GetAllServicesSortedWithLocation(
          this.getOngoingOnly,
          this.filter,
          'distanceAsc',
          false,
          18,
          lat,
          lng
        ).then((response) => {
          this.serviceResult = response.items;
          this.nestedServiceArray = this.SplitList();
          this.nextUrl = response.nextPage;
        });
          
      this.sortByDistance = true;
      
    },
  },
  props: {
    getByUser: Boolean,
    filter: String,
  },
  components: {
    Hero,
    BaseDropdown,
  },
};
</script>
