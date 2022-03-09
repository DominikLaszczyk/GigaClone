function printRadialTree(
    displayDirNames,
    displayNodes,
    displayFileNames,
    type1,
    type2,
    type3,
    moreLess,
    ccSize
) {
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


    let links = [];
    generateLinks(
        root.descendants(),
        links,
        type1,
        type2,
        type3,
        moreLess,
        ccSize
    );


    //tooltip
    var div = d3.select("body").append("div")
        .attr("class", "tooltip-donut")
        .style("opacity", 0);


    var radialLine = d3.lineRadial()
        .curve(d3.curveBundle.beta(0.85))
        .angle(function (d) {
            return d.x;
        })
        .radius(function (d) {
            return d.y;
        });


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
            // d3.linkRadial()
            //     .angle((d) => d.x)
            //     .radius((d) => d.y)
            function (d) {
                return radialLine([d.source, d.target]);
            }
        )
        .style("stroke", function (d) {return d.colour;})
        .style("stroke-width", function (d) {return d.width;})

        .on('mouseover', function(event, d) {
            d3.select(this).transition()
                .duration('50')
                .attr('opacity', '.85');
            //Makes the new div appear on hover:
            div.transition()
                .duration(50)
                .style("opacity", 1);
            div.html(d.sizes)
                .style("left", "10px")
                .style("top", "10px");
        })
        .on('mouseout', function (d) {
            d3.select(this).style("stroke", function (e) {return e.colour;});
            d3.select(this).style("stroke-width", function (e) {return e.width;});
            div.transition()
                .duration(500)
                .style("opacity", 0);
        });

    //nodes on files and dirs
    if(displayNodes) {
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
    }


    //names of files and dirs
    if (displayDirNames || displayFileNames) {

        let nodesWithText = []

        if(displayDirNames) {
            generateCloneRootDirs(root.descendants(), nodesWithText, moreLess, ccSize);
        }

        if(displayFileNames) {
            generateCloneFiles(root.descendants(), nodesWithText, moreLess, ccSize);
        }


        svg
            .append('g')
            .attr('font-family', 'sans-serif')
            .attr('font-size', 10)
            .attr('stroke-linejoin', 'round')
            .attr('stroke-width', 3)
            .selectAll('text')
            //.data(root.descendants())
            .data(nodesWithText)
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
}


function generateLinks(nodes, links, type1, type2, type3, moreLess, ccSize) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        if(typeof sizes !== 'undefined') {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if ((type1 && nodes[i].data.type === "1") || (type2 && nodes[i].data.type === "2") || (type3 && nodes[i].data.type === "3")) {
                    if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                        if (nodes[i].data.isClone === "1") {
                            links.push({
                                source: nodes[i].parent,
                                target: nodes[i],
                                colour: "orange",
                                width: "3.0",
                                sizes:sizes
                            })
                        }
                    }
                }
            }
        }
    }
}


function generateCloneRootDirs(nodes, cloneRootDirs, moreLess, ccSize) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        if(typeof sizes !== 'undefined') {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                    if (nodes[i].data.isRootCloneDir === "1") {
                        cloneRootDirs.push(nodes[i])
                    }
                }
            }
        }
    }
}

function generateCloneFiles(nodes, cloneRootDirs, moreLess, ccSize) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        if(typeof sizes !== 'undefined') {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                    if (nodes[i].data.isFile === "1") {
                        cloneRootDirs.push(nodes[i])
                    }
                }
            }
        }
    }
}


