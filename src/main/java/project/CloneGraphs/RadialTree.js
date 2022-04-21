function printRadialTree(
    cloneSizeColourCoding,
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

    d3.selectAll("div").remove()

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
        ccSize,
        maxSize,
        cloneSizeColourCoding
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
                .style("left", (event.pageX) + "px")
                .style("top", (event.pageY - 28) + "px");
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
            generateCloneRootDirs(root.descendants(), nodesWithText, moreLess, ccSize, type1, type2, type3);
        }

        if(displayFileNames) {
            generateCloneFiles(root.descendants(), nodesWithText, moreLess, ccSize, type1, type2, type3);
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


function generateLinks(nodes, links, type1, type2, type3, moreLess, ccSize, maxSize, cloneSizeColourCoding) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        let types = nodes[i].data.types
        let sizesSum = nodes[i].data.sizesSum
        if((typeof sizes !== 'undefined' && sizes.length > 0) && (typeof types !== 'undefined' && types.length > 0)) {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if ((type1 && types.includes(1)) || (type2 && types.includes(2))  || (type3 && types.includes(3))) {
                    if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                        if (nodes[i].data.isClone === "1") {
                            let link = {
                                source: nodes[i].parent,
                                target: nodes[i],
                                width: "3.0",
                                sizes: sizes,
                                sizesSum: sizesSum
                            }

                            if(cloneSizeColourCoding) {
                                let r = (255 * sizesSum)/maxSize
                                let g = (255 * (maxSize - sizesSum)) / maxSize
                                let b = 0;

                                let rgb = "rgb(" + r + "," + g + "," + b + ")"

                                if((type1 && types.includes(1)) ||
                                    (type2 && types.includes(2)) ||
                                    (type3 && types.includes(3))) {

                                    link.colour = rgb
                                }
                            }
                            else {
                                if (type1 && types.includes(1)) {
                                    link.colour = "orange";
                                }
                                else if(type2 && types.includes(2)) {
                                    link.colour = "blue";
                                }
                                else if(type3 && types.includes(3)) {
                                    link.colour = "green";
                                }
                            }



                            links.push(link)
                        }
                    }
                }
            }
        }
    }
}


function generateCloneRootDirs(nodes, cloneRootDirs, moreLess, ccSize, type1, type2, type3) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        let types = nodes[i].data.types
        if((typeof sizes !== 'undefined' && sizes.length > 0) && (typeof types !== 'undefined' && types.length > 0)) {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                    if ((type1 && types.includes(1)) || (type2 && types.includes(2))  || (type3 && types.includes(3))) {
                        if (nodes[i].data.isRootCloneDir === "1") {
                            cloneRootDirs.push(nodes[i])
                        }
                    }
                }
            }
        }
    }
}

function generateCloneFiles(nodes, cloneRootDirs, moreLess, ccSize, type1, type2, type3) {
    for(let i in nodes) {
        let sizes = nodes[i].data.sizes
        let types = nodes[i].data.types
        if((typeof sizes !== 'undefined' && sizes.length > 0) && (typeof types !== 'undefined' && types.length > 0)) {
            if ((moreLess === "More than" && sizes.some(el => el > ccSize)) || (moreLess === "Less than" && sizes.some(el => el < ccSize))) {
                if (nodes[i] !== null && typeof (nodes[i]) == "object" && nodes[i].parent !== null && typeof (nodes[i].parent) == "object") {
                    if ((type1 && types.includes(1)) || (type2 && types.includes(2))  || (type3 && types.includes(3))) {
                        if (nodes[i].data.isFile === "1") {
                            cloneRootDirs.push(nodes[i])
                        }
                    }
                }
            }
        }
    }
}


