import { Grid, Box, Tabs, Tab } from "@mui/material";

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export default function ReportTab({ tabValue, handleTabChange }) {
  return (
    <Grid xs={12}>
      <Box sx={{ width: "100%" }}>
        <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
          <Tabs
            value={tabValue}
            onChange={handleTabChange}
            aria-label="periodic report"
          >
            <Tab label="월간 보고서" {...a11yProps(0)} />
            <Tab label="연간 보고서" {...a11yProps(1)} />
          </Tabs>
        </Box>
      </Box>
    </Grid>
  );
}
