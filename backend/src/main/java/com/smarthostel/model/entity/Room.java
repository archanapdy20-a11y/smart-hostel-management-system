package com.smarthostel.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "room_type", length = 30)
    private String roomType;

    public Room() {}

    public Room(Long id, Block block, String roomNumber, Integer floor, Integer capacity, String roomType) {
        this.id = id;
        this.block = block;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
    }

    public static RoomBuilder builder() { return new RoomBuilder(); }

    public static class RoomBuilder {
        private Long id;
        private Block block;
        private String roomNumber;
        private Integer floor;
        private Integer capacity;
        private String roomType;

        public RoomBuilder id(Long id) { this.id = id; return this; }
        public RoomBuilder block(Block block) { this.block = block; return this; }
        public RoomBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public RoomBuilder floor(Integer floor) { this.floor = floor; return this; }
        public RoomBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public RoomBuilder roomType(String roomType) { this.roomType = roomType; return this; }

        public Room build() {
            return new Room(id, block, roomNumber, floor, capacity, roomType);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Block getBlock() { return block; }
    public void setBlock(Block block) { this.block = block; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}
