package com.smarthostel.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "blocks")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer floors;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warden_id")
    private Warden warden;

    public Block() {}

    public Block(Long id, Hostel hostel, String name, Integer floors, Warden warden) {
        this.id = id;
        this.hostel = hostel;
        this.name = name;
        this.floors = floors;
        this.warden = warden;
    }

    public static BlockBuilder builder() { return new BlockBuilder(); }

    public static class BlockBuilder {
        private Long id;
        private Hostel hostel;
        private String name;
        private Integer floors;
        private Warden warden;

        public BlockBuilder id(Long id) { this.id = id; return this; }
        public BlockBuilder hostel(Hostel hostel) { this.hostel = hostel; return this; }
        public BlockBuilder name(String name) { this.name = name; return this; }
        public BlockBuilder floors(Integer floors) { this.floors = floors; return this; }
        public BlockBuilder warden(Warden warden) { this.warden = warden; return this; }

        public Block build() {
            return new Block(id, hostel, name, floors, warden);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public Warden getWarden() { return warden; }
    public void setWarden(Warden warden) { this.warden = warden; }
}
