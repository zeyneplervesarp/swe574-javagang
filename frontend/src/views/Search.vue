<template>
  <div class="profile-page">
    <section class="section-profile-cover section-shaped my-0">
      <div class="shape shape-style-2 shape-primary shape-skew alpha-4">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </section>
    <section class="section section-skew">
      <div class="container">
        <card shadow class="card-profile mt--300" no-body>
          <div class="px-4">
            <div class="row justify-content-center">
              <div class="col-lg-3 order-lg-2">

              </div>
            </div>
            <div class="text-center mt-5">
              <h4>
                Search Results
                <span class="font-weight-light"
                  >({{ searchResultCount }} results have been found)</span
                >
              </h4>
            </div>
              
          </div>
            <div class="mt-5 py-5 border-top text-center">
              <div class="row">
                 <div class="col-md-3">
                 <!--<base-dropdown>
                    <base-button
                          slot="title"
                          type="warning"
                          class="dropdown-toggle float-right">
                          Filter By
                    </base-button>
                    <a class="dropdown-item" href="#">
                      Distance
                    </a>
                  </base-dropdown>-->
                </div>
                <div class="col-md-6">
                    <div class="form-group">

                    <GmapAutocomplete
                      class="form-control"
                      @place_changed="setPlace"
                      placeholder="Sort by Location"
                    />
                  </div>
              
               <div class="col-md-3">
               </div>
              </div>
              </div>
              <div class="row justify-content-center">
                <div class="col-lg-12">
                  <div class="container ct-example-row">
                    <div
                      class="row"
                      v-for="(result, index) in searchResult"
                      :key="index"
                    >
                      <div class="col mt-2 text-center">
                        <span v-if="result.matchType == 'USER'">
                          <base-button
                            @click="GoToUrl(result.url)"
                            block
                            type="info"
                            >{{ result.name }}
                            <badge class="float-right" pill type="info">{{
                              result.matchType
                            }}</badge>
                          </base-button>
                        </span>

                        <span v-else>
                          <base-button
                            @click="GoToUrl(result.url)"
                            block
                            type="success"
                            >{{ result.name }}

                            <badge class="float-right" pill type="success">{{
                              result.matchType
                            }}</badge>
                          </base-button>
                        </span>
                      </div>
                      <div class="w-100"></div>
                    </div>
                  </div>
                </div>
              </div>
          </div>
        </card>
      </div>
    </section>
  </div>
</template>
<script>
import apiRegister from "../api/register";
import BaseDropdown from '../components/BaseDropdown.vue';

export default {
  components: {BaseDropdown},
  data() {
    return {
      searchResult: [],
      searchResultCount: 0,
    };
  },
  mounted() {
    this.GetSearchResult();
  },
  methods: {
    GetSearchResult() {
      var searchQuery = this.$route.params.search_query;
      apiRegister.Search(searchQuery).then((r) => {
        this.searchResult = r;
        this.searchResultCount = r.length;
      });
    },
    GoToUrl(url) {
      url = url.replace("user", "profile");
      url = "#" + url;
      window.location.href = url;
    },
    setPlace(place) {
      var lat = place.geometry.location.lat();
      var lng = place.geometry.location.lng();
      var name = place.name;
      var formattedAddress = place.formatted_address;
      var searchQuery = this.$route.params.search_query;
      this.FilterSearchResultsByLocation(searchQuery,lat,lng);
      debugger;
      this.$router.go();
    },
    FilterSearchResultsByLocation(searchQuery,lat,lon){
      apiRegister.SearchWithLocationPerimeter(searchQuery,lat,lon).then((r) => {
        this.searchResult = r;
        this.searchResultCount = r.length;
      });
    },
  },
  props: {
    search_query: String,
  },
};
</script>
<style>
</style>
