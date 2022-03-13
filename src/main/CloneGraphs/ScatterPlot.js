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
    //alert(labels);

    const width = 700,
        height = 700

    const cloneGraph = document.getElementById("chart");
    cloneGraph.textContent = '';

    var svg = d3
        .select("svg")
        .attr('width', '100%')
        .attr('height', '100%')
        .append('g')
        .attr('transform', 'translate(' + 0 + ',' + 0 + ')');
        // margin = 200,
        // width = svg.attr("width") - margin, //300
        // height = svg.attr("height") - margin //200

    // Step 4
    var xScale = d3.scaleLinear().domain([0, labels.length-1]).range([0, width]),
        yScale = d3.scaleLinear().domain([0, labels.length-1]).range([height, 0]);



    // // plot Title
    // svg.append('text')
    //     .attr('x', width/2 + 100)
    //     .attr('y', 100)
    //     .attr('text-anchor', 'middle')
    //     .style('font-family', 'Helvetica')
    //     .style('font-size', 20)
    //     .text('Scatter Plot');
    //
    // // X label
    // svg.append('text')
    //     .attr('x', width/2 + 100)
    //     .attr('y', height - 15 + 150)
    //     .attr('text-anchor', 'middle')
    //     .style('font-family', 'Helvetica')
    //     .style('font-size', 12)
    //     .text('Independant');
    //
    // // Y label
    // svg.append('text')
    //     .attr('text-anchor', 'middle')
    //     .attr('transform', 'translate(60,' + height + ')rotate(-90)')
    //     .style('font-family', 'Helvetica')
    //     .style('font-size', 12)
    //     .text('Dependant');



    var linear = d3.scaleLinear()
        .domain([0, labels.length-1])
        .range([0, width]);

    // x and y axes
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(linear)
            .tickFormat(function (d) {
                return labels[d];
            })
        );

    svg.append("g")
        .call(d3.axisLeft(linear)
            .tickFormat(function (d) {
                return labels[d];
            })
        );


    svg
        .attr('width', '100%')
        .attr('height', '100%')
        .append("g")
        .selectAll("dot")
        .data(data)
        .enter()
        .append("circle")
        .attr("cx", function (d) { return xScale(d[0]); } )
        .attr("cy", function (d) { return yScale(d[1]); } )
        .attr("r", 2)
        .attr("transform", "translate(" + 0 + "," + 0 + ")")
        .style("fill", "#cc0000");
}