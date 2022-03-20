function printScatterPlot(
    displayDirNames,
    displayNodes,
    displayFileNames,
    type1,
    type2,
    type3,
    moreLess,
    ccSize
) {
    alert("SCATTER PLOT");

    const width = 700,
        height = 700

    let dots = generateDots(
        data,
        type1,
        type2,
        type3,
        moreLess,
        ccSize
    );

    const cloneGraph = document.getElementById("chart");
    cloneGraph.textContent = '';

    // add the tooltip area
    var tooltip = d3.select("body").append("div")
        .style("position", "absolute")
        .attr("class", "tooltip")
        .style("opacity", 0);

    var svg = d3
        .select("svg")
        .attr('width', '100%')
        .attr('height', '100%')
        .append('g')
        .attr('transform', 'translate(' + 0 + ',' + 0 + ')')

    var xScale = d3.scaleLinear().domain([0, labels.length-1]).range([width, 0]),
        yScale = d3.scaleLinear().domain([0, labels.length-1]).range([height, 0]);

    var linear = d3.scaleLinear()
        .domain([0, labels.length-1])
        .range([0, width]);

    // x and y axes
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(linear));

    svg.append("g")
        .call(d3.axisLeft(linear));

    //dots
    svg
        .attr('width', '100%')
        .attr('height', '100%')
        .append("g")
        .selectAll("dot")
        .data(dots)
        .enter()
        .append("circle")
        .attr("cx", function (d) { return xScale(d.fileIndex1); } )
        .attr("cy", function (d) { return yScale(d.fileIndex2); } )
        .attr("r", 2)
        .attr("transform", "translate(" + 0 + "," + 0 + ")")
        .style("fill", function (e) {return e.colour;})
        .on("mouseover", function(event, d) {
            tooltip
                .style("left", (event.pageX+5) + "px")
                .style("top", (event.pageY-37) + "px")
                .style("opacity", .9)
                .html("File 1: " + d.fileName1 + "<br>" +
                    "File 2: " + d.fileName2 + "<br>" +
                    "");
        })
        .on("mouseout", function(event, d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });
}

function generateDots(data, type1, type2, type3, moreLess, ccSize) {
    let dots = []
    for(let i=0; i<data.length; i++) {
        if((moreLess === "More than" && data[i].size > ccSize) || (moreLess === "Less than" && data[i].size < ccSize)) {
            if(type1 && data[i].type === 1) {
                data[i].colour = "orange"
                dots.push(data[i])
            }
            if(type2 && data[i].type === 2) {
                data[i].colour = "blue"
                dots.push(data[i])
            }
            if(type3 && data[i].type === 3) {
                data[i].colour = "green"
                dots.push(data[i])
            }
        }

    }

    return dots
}