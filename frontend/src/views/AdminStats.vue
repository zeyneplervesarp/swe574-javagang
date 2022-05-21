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
            <GChart
              type="LineChart"
              :options="options"
              :data="collectionData"
            />
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

export default {
  components: { BaseButton, GChart },
  data() {
    return {
      stats: null,
      dailyStats: null,
      itemKeys: null,
      itemKeysPretty: null,
      collectionData: [],
      options: {
        chart: {
          title: "Daily stats",
          subtitle: "",
        },
        width: 1000,
        height: 400,
      },
    };
  },
  mounted() {
    this.GetAllStats();
    this.GetDailyStats();
  },
  computed: {},
  methods: {
    GetAllStats() {
      apiRegister.GetAllStats().then((stats) => {
        this.stats = stats;
      });
    },
    GetDailyStats() {
      apiRegister.GetDailyStats("dailystats").then((dailyStats) => {
        console.log("fetched daily stats: ", dailyStats);
        const items = dailyStats.items;
        const itemKeys = Object.keys(items);
        this.itemKeys = itemKeys;
        this.itemKeysPretty = itemKeys.map(
              (key) =>
                key.charAt(0).toUpperCase() +
                key.replace(/([A-Z])/g, " $1").slice(1)
            );
        let days = Object.keys(items[itemKeys[0]]);
        days.sort();
        const collectionData = [
          [
            "Day",
            ...this.itemKeysPretty,
          ],
          ...days.map((day) => [day]),
        ];
        for (let i = 1; i < collectionData.length; i++) {
          const curArr = collectionData[i];
          const curDay = curArr[0];
          itemKeys.forEach((key) => {
            curArr.push(items[key][curDay]);
          });
        }

        this.collectionData = collectionData;
      });
    },
  },
};
</script>
<style>
</style>
