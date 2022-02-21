function printRadialTree() {
    alert("RADIAL TREE");
    const cloneGraph = document.getElementById("chart");
    cloneGraph.textContent = '';

    const width = 750,
        height = 750,
        radius = width / 2;

    const tree = d3.cluster().size([2 * Math.PI, radius - 100]);

    const root = tree(
        d3
            .hierarchy(data)
            .sort((a, b) => d3.ascending(a.data.name, b.data.name))
    );

    //let nodes = generateLinks(data);

    //that's all... no magic, no bloated framework
    let links = [];

    generateLinks(root.descendants(), links);

    //alert(links);
   // alert(root.links());


    const svg = d3
        .select('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .append('g')
        .attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')');

    //edges
    svg
        .append('g')
        .attr('fill', 'none')
        .attr('stroke', '#555')
        .attr('stroke-opacity', 0.4)
        .attr('stroke-width', 1.5)
        .selectAll('path')
        .data(links)
        .join('path')
        .attr(
            'd',
            // d3.radialLine()
            //     .angle((d) => d.x)
            //     .radius((d) => d.y)
                // .x(function(d) { return d.x; })
                // .y(function(d) { return d.y; })
                // .curve(d3.curveLinear)
            d3.linkRadial()
                //.curve(d3.curveBundle.beta(0.85))
                .angle((d) => d.x)
                .radius((d) => d.y)

)
        .style("stroke", function(d){
            return d.colour;
        })
        .style("stroke-width", function(d){
            return d.width;
        })
        .on('mouseover', function(d) {
            d3.select(this).style("stroke", "blue");
            d3.select(this).style("stroke-width", 7.0);

        })
        .on('mouseout', function(d) {
            d3.select(this).style("stroke", function(e) {
                return e.colour;
            });
            d3.select(this).style("stroke-width", function(e) {
                return e.width;
            });
        });

    svg
        .append('g')
        .selectAll('circle')
        .data(root.descendants())
        .join('circle')
        .attr(
            'transform',
            (d) => `
                rotate(${(d.x * 180) / Math.PI - 90})
                translate(${d.y},0)
              `
        )
        .attr('fill', (d) => (d.children ? '#555' : '#999'))
        .attr('r', 2.0);

    svg
        .append('g')
        .attr('font-family', 'sans-serif')
        .attr('font-size', 10)
        .attr('stroke-linejoin', 'round')
        .attr('stroke-width', 3)
        .selectAll('text')
        .data(root.descendants())
        .join('text')
        .attr(
            'transform',
            (d) => `
                rotate(${(d.x * 180) / Math.PI - 90})
                translate(${d.y},0)
                rotate(${d.x >= Math.PI ? 180 : 0})
              `
        )
        .attr('dy', '0.31em')
        .attr('x', (d) => (d.x < Math.PI === !d.children ? 6 : -6))
        .attr('text-anchor', (d) =>
            d.x < Math.PI === !d.children ? 'start' : 'end'
        )
        .text((d) => d.data.name)
        .clone(true)
        .lower()
        .attr('stroke', 'white');
}


function generateLinks(nodes, links) {
    for(let i in nodes) {
        if (nodes[i] !== null && typeof(nodes[i])=="object" && nodes[i].parent !== null && typeof(nodes[i].parent)=="object") {
            if(nodes[i].data.value === "1") {
                links.push({source:nodes[i].parent, target:nodes[i], colour:"orange", width:"3.0"})
            }
            else {
                links.push({source:nodes[i].parent, target:nodes[i], colour:"grey", width:"1.0"})
            }
        }
    }
}

