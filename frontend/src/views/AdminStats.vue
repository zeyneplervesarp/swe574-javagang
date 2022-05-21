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
            <div class="text-center mt-5">
              <h3>socialHub in numbers</h3>
              <div></div>
              <br />
              <div class="text-center">
                <div>
                  <table class="table table-striped" v-if="stats != null">
                    <thead class="">
                      <tr>
                        <th scope="col"> </th>
                        <th scope="col">Last 24 Hours</th>
                        <th scope="col">Last 7 Days</th>
                        <th scope="col">Last 30 Days</th>
                        <th scope="col">All Time</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(stat, key) in stats" :key="key">
                        <td>{{ key.charAt(0).toUpperCase() + key.replace(/([A-Z])/g, " $1").slice(1) }}</td>
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
import swal from "sweetalert2";

export default {
  components: { BaseButton },
  data() {
    return {
      stats: null,
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
      })
    }
  },
};
</script>
<style>
</style>
