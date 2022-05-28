<template>
  <div class="profile-page">
    <section class="section-profile-cover section-shaped my-0">
      <div class="shape shape-style-3 shape-primary shape-skew alpha-4">
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
              <div class="col-lg-3 order-lg-2"></div>
              <div
                class="col-lg-4 order-lg-3 text-lg-right align-self-lg-center"
              ></div>
            </div>
            <div class="text-center mt-5" v-if="this.itemKeysPretty != null">
              <h3>socialHub with charts</h3>
              <div></div>
              <br />
              <div class="row pull-left">
                <base-dropdown>
                  <base-button
                    slot="title"
                    type="warning"
                    class="dropdown-toggle float-right"
                  >
                    Metric
                  </base-button>

                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(
                        itemKeys,
                        itemKeysPretty,
                        timeFilterInUse
                      )
                    "
                  >
                    All<span class="btn-inner--icon"
                      ><i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>
                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(
                        [itemKeys[index]],
                        [itemKeyPretty],
                        timeFilterInUse
                      )
                    "
                    v-for="(itemKeyPretty, index) in this.itemKeysPretty"
                    :key="index"
                  >
                    {{ itemKeyPretty }}
                    <span class="btn-inner--icon">
                      <i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>
                </base-dropdown>

                <base-dropdown>
                  <base-button
                    slot="title"
                    type="warning"
                    class="dropdown-toggle float-right"
                  >
                    Timespan
                  </base-button>

                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(itemKeysInUse, itemKeysPrettyInUse)
                    "
                  >
                    All Time<span class="btn-inner--icon"
                      ><i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>

                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(itemKeysInUse, itemKeysPrettyInUse, {
                        gt: GetFormattedDaysAgo(7),
                      })
                    "
                  >
                    Last 7 Days<span class="btn-inner--icon"
                      ><i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>

                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(itemKeysInUse, itemKeysPrettyInUse, {
                        gt: GetFormattedDaysAgo(30),
                      })
                    "
                  >
                    Last 30 Days<span class="btn-inner--icon"
                      ><i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>

                  <p
                    class="dropdown-item"
                    v-on:click="
                      MakeDataForItemKeys(itemKeysInUse, itemKeysPrettyInUse, {
                        gt: GetFormattedDaysAgo(90),
                      })
                    "
                  >
                    Last 90 Days<span class="btn-inner--icon"
                      ><i class="fa fa- fa-sort-amount-asc mr-2"></i>
                    </span>
                  </p>
                </base-dropdown>
              </div>

              <div class="text-center mt-5" v-if="this.itemKeysPretty != null">
                <GChart
                  type="LineChart"
                  :options="options"
                  :data="collectionData"
                />
              </div>
            </div>
            <div class="text-center mt-5">
              <h3>socialHub in numbers</h3>
              <div></div>
              <br />
              <div class="text-center">
                <div>
                  <table class="table table-striped" v-if="stats != null">
                    <thead class="">
                      <tr>
                        <th scope="col"></th>
                        <th scope="col">Last 24 Hours</th>
                        <th scope="col">Last 7 Days</th>
                        <th scope="col">Last 30 Days</th>
                        <th scope="col">All Time</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(stat, key) in stats" :key="key">
                        <td>
                          {{
                            key.charAt(0).toUpperCase() +
                            key.replace(/([A-Z])/g, " $1").slice(1)
                          }}
                        </td>
                        <td>{{ stat.last24HoursCount }}</td>
                        <td>{{ stat.lastWeekCount }}</td>
                        <td>{{ stat.lastMonthCount }}</td>
                        <td>{{ stat.totalCount }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <br />
              <div></div>
            </div>
            <div v-if="services != null" class="text-center mt-5">
              <h3>socialHub on map</h3>
              <div></div>
              <br />
              <div class="text-center">
                <div class="justify-content-center">
                  <base-button type="secondary">
                    <GmapMap
                      :center="map_center"
                      :zoom="2"
                      map-type-id="terrain"
                      style="width: 800px; height: 400px"
                    >
                      <gmap-info-window
                        :options="{
                          maxWidth: 300,
                          pixelOffset: { width: 0, height: -35 },
                        }"
                        :position="infoWindow.position"
                        :opened="infoWindow.open"
                        @closeclick="infoWindow.open = false"
                      >
                        <div v-html="infoWindow.template"></div>
                      </gmap-info-window>
                      <GmapMarker
                        v-for="(service, index) in services"
                        :key="index"
                        :position="GetPosition(service)"
                        :clickable="true"
                        :draggable="false"
                        @click="openInfoWindowTemplate(service)"
                      />
                    </GmapMap>
                  </base-button>
                </div>
              </div>
              <br />
              <div></div>
            </div>
          </div>
        </card>
      </div>
    </section>
  </div>
</template>
<script>
import BaseButton from "../components/BaseButton.vue";
import apiRegister from "../api/register";
import { GChart } from "vue-google-charts/legacy";
import BaseDropdown from "@/components/BaseDropdown";
import moment from "moment";

export default {
  components: { BaseButton, GChart, BaseDropdown },
  data() {
    return {
      stats: null,
      dailyStats: null,
      itemKeys: null,
      itemKeysPretty: null,
      timeFilterInUse: null,
      itemKeysInUse: null,
      itemKeysPrettyInUse: null,
      collectionData: [],
      options: {
        chart: {
          title: "Daily stats",
          subtitle: "",
        },
        height: 600,
      },
      services: null,
      map_center: { lat: 10, lng: 10 },
      infoWindow: {
        position: { lat: 0, lng: 0 },
        open: false,
        template: "",
      },
    };
  },
  mounted() {
    this.GetAllStats();
    this.GetDailyStats();
    this.GetMapData();
  },
  computed: {},
  methods: {
    GetAllStats() {
      apiRegister.GetAllStats().then((stats) => {
        this.stats = stats;
      });
    },
    Print(any) {
      console.log("Printing: ", any);
    },
    GetFormattedDaysAgo(daysAgo) {
      return moment().subtract(daysAgo, "days").format("YYYY-MM-DD");
    },
    MakeDataForItemKeys(itemKeys, itemKeysPretty, timeFilter) {
      if (timeFilter) {
        this.timeFilterInUse = timeFilter;
      }

      this.itemKeysInUse = itemKeys;
      this.itemKeysPrettyInUse = itemKeysPretty;

      let daySet = new Set();

      Object.keys(this.dailyStats.items).forEach((ik) =>
        Object.keys(this.dailyStats.items[ik]).forEach((d) => daySet.add(d))
      );
      let days = Array.from(daySet);
      days.sort();
      if (timeFilter) {
        console.log("timeFilterGt: ", timeFilter.gt);
        days = days.filter((d) => d >= timeFilter.gt);
      }

      const collectionData = [
        ["Day", ...itemKeysPretty],
        ...days.map((day) => [day]),
      ];
      for (let i = 1; i < collectionData.length; i++) {
        const curArr = collectionData[i];
        const curDay = curArr[0];
        itemKeys.forEach((key) => {
          let curStat = this.dailyStats.items[key][curDay];
          if (!curStat) {
            curStat = 0;
          }
          curArr.push(curStat);
        });
      }
      this.collectionData = collectionData;
    },
    GetDailyStats() {
      apiRegister.GetDailyStats("dailystats").then((dailyStats) => {
        this.dailyStats = dailyStats;
        console.log("fetched daily stats: ", dailyStats);
        const items = dailyStats.items;
        const itemKeys = Object.keys(items);
        this.itemKeys = itemKeys;
        this.itemKeysPretty = itemKeys.map(
          (key) =>
            key.charAt(0).toUpperCase() +
            key.replace(/([A-Z])/g, " $1").slice(1)
        );
        this.MakeDataForItemKeys(this.itemKeys, this.itemKeysPretty);
      });
    },
    GetMapData() {
      apiRegister.GetServiceDashboard().then((response) => {
        this.services = response;
      });
    },
    GetPosition(marker) {
      return {
        lat: parseFloat(marker.latitude),
        lng: parseFloat(marker.longitude),
      };
    },
    ChangeCenter(service) {
      this.map_center.lat = service.latitude;
      this.map_center.lng = service.longitude;
    },
    openInfoWindowTemplate(service) {
      this.map_center.lat = service.latitude;
      this.map_center.lng = service.longitude;
      this.infoWindow.position.lat = service.latitude;
      this.infoWindow.position.lng = service.longitude;
      this.infoWindow.template = `<br><b>${service.header}</b><br>${service.location}<br><a target="_blank" href="#/service/`+service.id+`">View Service</a><br>`;
      this.infoWindow.open = true;
    },
  },
};
</script>
<style>
</style>
