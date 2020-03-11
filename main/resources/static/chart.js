window.addEventListener('load', setup);

async function setup() {
    const ctx = document.getElementById('myChart').getContext('2d');
    const dataTemps = await getData();
    document.getElementById('NewCases').innerText = dataTemps.newCases[dataTemps.newCases.length - 1];
    const myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dataTemps.dates,
            datasets: [{
                label: 'Total Cases',
                data: dataTemps.totalCases,
                fill: false,
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.5)',
                borderWidth: 1
            },
                {
                    label: 'New Cases',
                    data: dataTemps.newCases,
                    fill: false,
                    borderColor: 'rgba(99, 132, 255, 1)',
                    backgroundColor: 'rgba(99, 132, 255, 0.5)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true
        }
    });
}

async function getData() {
    // const response = await fetch('https://raw.githubusercontent.com/CodingTrain/Intro-to-Data-APIs-JS/source/module1/02_fetch_csv_exercise_multiple/testdata.csv');
    const response = await fetch(
        'https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv'
    );
    const data = await response.text();
    const dates = [];
    const county = [];
    const state = [];
    const country = [];
    const totalCases = [];
    const newCases = [];
    const rows = data.split('\n').slice(1);
    const headers = data.split('\n')[0];
    console.log(headers);
    // dates.push(header.split(',').slice(4, header.length - 1));
    for (let i = 4; i < headers.length; i++) {
        const cols = headers.split(',');
        if (cols[i] !== undefined) dates.push(cols[i]);
    }
    console.log(dates);
    rows.forEach(row => {
        const cols = row.split(',');
        if (cols[2] !== "US") {
        } else {
            county.push(cols[0].substring(1,));
            state.push(cols[1].substring(1, 3));
            country.push(cols[2]);
            for (let i = 5; i < cols.length; i++) {
                if (cols[i] === "") {
                    continue;
                } else if (totalCases[i - 5] == null) {
                    totalCases.push(parseInt(cols[i]));
                } else {
                    totalCases[i - 5] += parseInt(cols[i]);
                }
                // console.log(i + " case: " + cols[i]);
            }
        }

        if (cols[1] === "US") { //ships
            for (let i = 4; i < cols.length; i++) {
                if (cols[i] === "") {
                    continue;
                } else if (totalCases[i - 4] == null) {
                    totalCases.push(parseInt(cols[i]));
                } else {
                    totalCases[i - 4] += parseInt(cols[i]);
                }
            }
        }
    });

    for (let i = 0; i < totalCases.length; i++) {
        if (i === 0) {
            newCases.push(0);
        } else {
            newCases.push(totalCases[i] - totalCases[i - 1]);
        }

    }
    console.log(totalCases);
    console.log(newCases);
    return {
        dates,
        county,
        state,
        country,
        totalCases,
        newCases
    };
}